import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Livro, LivroRequest } from '../../models/livro';
import { Autor } from '../../models/autor';
import { Assunto } from '../../models/assunto';
import { LivroService } from '../../services/livro.service';
import { AutorService } from '../../services/autor.service';
import { AssuntoService } from '../../services/assunto.service';
import {NgSelectComponent, NgSelectModule} from "@ng-select/ng-select";

@Component({
  selector: 'app-livros',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgSelectModule],
  templateUrl: './livros.component.html',
  styleUrl: './livros.component.css'
})
export class LivrosComponent implements OnInit {


  livros: Livro[] = [];
  autores: Autor[] = [];
  assuntos: Assunto[] = [];

  loading = false;
  editingId: number | null = null;
  error: string | null = null;

  form = this.fb.group({
    titulo: ['', [Validators.required, Validators.maxLength(40)]],
    editora: ['', [Validators.required, Validators.maxLength(40)]],
    edicao: [1, [Validators.required, Validators.min(1)]],
    anoPublicacao: ['', [Validators.required, Validators.pattern(/^\d{4}$/)]],
    valor: [0, [Validators.required, Validators.min(0)]],
    autoresIds: this.fb.control<number[]>([], [Validators.required, Validators.minLength(1)]),
    assuntosIds: this.fb.control<number[]>([], [Validators.required, Validators.minLength(1)])
  });

  constructor(
    private fb: FormBuilder,
    private livroService: LivroService,
    private autorService: AutorService,
    private assuntoService: AssuntoService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.error = null;

    this.autorService.list().subscribe({ next: a => this.autores = a });
    this.assuntoService.list().subscribe({ next: a => this.assuntos = a });

    this.livroService.list().subscribe({
      next: (data) => this.livros = data,
      error: () => this.error = 'Falha ao carregar livros.',
      complete: () => this.loading = false
    });
  }

  startEdit(l: Livro): void {
    this.editingId = l.id;
    this.form.patchValue({
      titulo: l.titulo,
      editora: l.editora,
      edicao: l.edicao,
      anoPublicacao: l.anoPublicacao,
      valor: l.valor,
      autoresIds: l.autores.map(a => a.id),
      assuntosIds: l.assuntos.map(a => a.id)
    });
  }

  cancel(): void {
    this.editingId = null;
    this.form.reset({
      titulo: '',
      editora: '',
      edicao: 1,
      anoPublicacao: '',
      valor: 0,
      autoresIds: [],
      assuntosIds: []
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error = 'Preencha os campos obrigatórios.';
      return;
    }

    const v = this.form.value;
    const req: LivroRequest = {
      titulo: (v.titulo || '').trim(),
      editora: (v.editora || '').trim(),
      edicao: Number(v.edicao),
      anoPublicacao: (v.anoPublicacao || '').trim(),
      valor: Number(v.valor),
      autoresIds: v.autoresIds || [],
      assuntosIds: v.assuntosIds || []
    };

    this.loading = true;
    this.error = null;

    const call$ = this.editingId
      ? this.livroService.update(this.editingId, req)
      : this.livroService.create(req);

    call$.subscribe({
      next: () => { this.cancel(); this.loadAll(); },
      error: () => this.error = 'Falha ao salvar livro (verifique vínculos/autores/assuntos).',
      complete: () => this.loading = false
    });
  }

  remove(l: Livro): void {
    if (!confirm(`Remover livro "${l.titulo}"?`)) return;
    this.loading = true;
    this.error = null;
    this.livroService.delete(l.id).subscribe({
      next: () => this.loadAll(),
      error: () => this.error = 'Falha ao remover livro.',
      complete: () => this.loading = false
    });
  }

  getAutoresTexto(l: Livro): string {
    return l.autores?.map(a => a.nome).join(', ') || '';
  }

  getAssuntosTexto(l: Livro): string {
    return l.assuntos?.map(a => a.descricao).join(', ') || '';
  }

}
