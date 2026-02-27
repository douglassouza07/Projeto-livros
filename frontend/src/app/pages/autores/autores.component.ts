import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AutorService } from '../../services/autor.service';
import { Autor } from '../../models/autor';

@Component({
  selector: 'app-autores',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './autores.component.html',
  styleUrl: './autores.component.css'
})
export class AutoresComponent implements OnInit {

  autores: Autor[] = [];
  loading = false;
  editingId: number | null = null;
  error: string | null = null;

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(40)]]
  });

  constructor(private fb: FormBuilder, private service: AutorService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.service.list().subscribe({
      next: (data) => this.autores = data,
      error: () => this.error = 'Falha ao carregar autores.',
      complete: () => this.loading = false
    });
  }

  startEdit(a: Autor): void {
    this.editingId = a.id;
    this.form.setValue({ nome: a.nome });
  }

  cancel(): void {
    this.editingId = null;
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const nome = this.form.value.nome!.trim();

    this.loading = true;
    this.error = null;

    const req$ = this.editingId
      ? this.service.update(this.editingId, nome)
      : this.service.create(nome);

    req$.subscribe({
      next: () => { this.cancel(); this.load(); },
      error: () => this.error = 'Falha ao salvar autor.',
      complete: () => this.loading = false
    });
  }

  remove(a: Autor): void {
    if (!confirm(`Remover autor "${a.nome}"?`)) return;
    this.loading = true;
    this.error = null;
    this.service.delete(a.id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Falha ao remover autor (pode estar vinculado a livro).',
      complete: () => this.loading = false
    });
  }
}
