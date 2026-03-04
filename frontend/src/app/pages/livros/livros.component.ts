import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Livro, LivroRequest } from '../../models/livro';
import { Autor } from '../../models/autor';
import { Assunto } from '../../models/assunto';
import { LivroService } from '../../services/livro.service';
import { AutorService } from '../../services/autor.service';
import { AssuntoService } from '../../services/assunto.service';
import { NgSelectModule } from '@ng-select/ng-select';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil, tap } from 'rxjs/operators';
import { ToastService } from '../../core/toast/toast.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-livros',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgSelectModule],
  templateUrl: './livros.component.html',
  styleUrl: './livros.component.css'
})
export class LivrosComponent implements OnInit {
  livros: Livro[] = [];

  // opções do autocomplete (remoto)
  autoresOptions: Autor[] = [];
  assuntosOptions: Assunto[] = [];
  readonly autorTypeahead$ = new Subject<string>();
  readonly assuntoTypeahead$ = new Subject<string>();

  // paginação + filtro
  pageSize = 10;
  page = 1;
  searchTerm = '';

  loading = false;
  loadingOptions = false;
  editingId: number | null = null;
  error: string | null = null; // apenas validação/form

  years: number[] = [];

  private readonly destroy$ = new Subject<void>();

  // form tipado (nonNullable)
  form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(40)]],
    editora: ['', [Validators.required, Validators.maxLength(40)]],
    edicao: this.fb.nonNullable.control(1, [Validators.required, Validators.min(1)]),
    anoPublicacao: this.fb.nonNullable.control(new Date().getFullYear(), [Validators.required]),
    valor: this.fb.nonNullable.control(0, [Validators.required, Validators.min(0)]),
    autoresIds: this.fb.nonNullable.control<number[]>([], [Validators.required, Validators.minLength(1)]),
    assuntosIds: this.fb.nonNullable.control<number[]>([], [Validators.required, Validators.minLength(1)]),
  }, {
    validators: []
  });

  constructor(
    private fb: FormBuilder,
    private livroService: LivroService,
    private autorService: AutorService,
    private assuntoService: AssuntoService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    // anos (1900..2100)
    this.years = Array.from({ length: 2100 - 1900 + 1 }, (_, i) => 1900 + i);

    // autocomplete remoto (debounce + cache no service)
    this.autorTypeahead$
      .pipe(
        debounceTime(250),
        distinctUntilChanged(),
        tap(() => (this.loadingOptions = true)),
        switchMap(term => this.autorService.search(term)),
        takeUntil(this.destroy$)
      )
      .subscribe(list => {
        this.autoresOptions = list;
        this.loadingOptions = false;
      });

    this.assuntoTypeahead$
      .pipe(
        debounceTime(250),
        distinctUntilChanged(),
        tap(() => (this.loadingOptions = true)),
        switchMap(term => this.assuntoService.search(term)),
        takeUntil(this.destroy$)
      )
      .subscribe(list => {
        this.assuntosOptions = list;
        this.loadingOptions = false;
      });

    // primeira carga das opções
    this.autorTypeahead$.next('');
    this.assuntoTypeahead$.next('');

    this.loadAll();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAll(): void {
    this.loading = true;
    this.error = null;

    this.livroService.list().subscribe({
      next: (data) => {
        this.livros = data;
        this.page = 1;
      },
      error: () => this.toast.error('Falha ao carregar livros.'),
      complete: () => this.loading = false
    });
  }

  startEdit(l: Livro): void {
    this.editingId = l.id;
    this.form.patchValue({
      titulo: l.titulo,
      editora: l.editora,
      edicao: Number(l.edicao),
      anoPublicacao: Number(l.anoPublicacao),
      valor: Number(l.valor),
      autoresIds: l.autores.map(a => a.id),
      assuntosIds: l.assuntos.map(a => a.id)
    });
  }

  cancel(): void {
    this.editingId = null;
    this.form.reset();
    this.form.patchValue({
      titulo: '',
      editora: '',
      edicao: 1,
      anoPublicacao: new Date().getFullYear(),
      valor: 0,
      autoresIds: [],
      assuntosIds: [],
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error = 'Preencha os campos obrigatórios.';
      this.toast.warning('Preencha os campos obrigatórios.');
      return;
    }

    // valida ano e limites
    const ano = Number(this.form.controls.anoPublicacao.value);
    if (!Number.isFinite(ano) || ano < 1900 || ano > 2100) {
      this.form.controls.anoPublicacao.setErrors({ year: true });
      this.toast.warning('Informe um ano válido (1900 a 2100).');
      return;
    }

    const ed = Number(this.form.controls.edicao.value);
    if (!Number.isFinite(ed) || ed < 1) {
      this.form.controls.edicao.setErrors({ min: true });
      this.toast.warning('Edição deve ser maior ou igual a 1.');
      return;
    }

    const val = Number(this.form.controls.valor.value);
    if (!Number.isFinite(val) || val < 0) {
      this.form.controls.valor.setErrors({ min: true });
      this.toast.warning('Valor deve ser maior ou igual a 0.');
      return;
    }

    const v = this.form.getRawValue();
    const req: LivroRequest = {
      titulo: v.titulo.trim(),
      editora: v.editora.trim(),
      edicao: Number(v.edicao),
      anoPublicacao: Number(v.anoPublicacao),
      valor: Number(v.valor),
      autoresIds: v.autoresIds,
      assuntosIds: v.assuntosIds,
    };

    this.loading = true;
    this.error = null;

    const call$ = this.editingId
      ? this.livroService.update(this.editingId, req)
      : this.livroService.create(req);

    call$.subscribe({
      next: () => {
        this.toast.success(this.editingId ? 'Livro atualizado.' : 'Livro criado.');
        this.cancel();
        this.loadAll();
      },
      error: () => this.error = 'Falha ao salvar livro.',
      complete: () => this.loading = false
    });
    this.loading = false
  }

  remove(l: Livro): void {
    if (!confirm(`Remover livro "${l.titulo}"?`)) return;
    this.loading = true;
    this.error = null;
    this.livroService.delete(l.id).subscribe({
      next: () => {
        this.toast.success('Livro removido.');
        this.loadAll();
      },
      error: () => this.toast.error('Falha ao remover livro.'),
      complete: () => this.loading = false
    });
    this.loading = false
  }

  // Lista filtrada + paginada
  get filteredLivros(): Livro[] {
    const term = (this.searchTerm || '').trim().toLowerCase();
    if (!term) return this.livros;
    return this.livros.filter(l => {
      const autores = (l.autores || []).map(a => a.nome).join(' ').toLowerCase();
      const assuntos = (l.assuntos || []).map(a => a.descricao).join(' ').toLowerCase();
      return (
        (l.titulo || '').toLowerCase().includes(term) ||
        (l.editora || '').toLowerCase().includes(term) ||
        autores.includes(term) ||
        assuntos.includes(term)
      );
    });
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredLivros.length / this.pageSize));
  }

  get pagedLivros(): Livro[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredLivros.slice(start, start + this.pageSize);
  }

  goToPage(p: number): void {
    this.page = Math.min(Math.max(1, p), this.totalPages);
  }

  getAutoresTexto(l: Livro): string {
    return l.autores?.map(a => a.nome).join(', ') || '';
  }

  getAssuntosTexto(l: Livro): string {
    return l.assuntos?.map(a => a.descricao).join(', ') || '';
  }

}
