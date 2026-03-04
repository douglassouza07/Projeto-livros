import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AutorService } from '../../services/autor.service';
import { Autor } from '../../models/autor';
import { ToastService } from '../../core/toast/toast.service';

@Component({
  selector: 'app-autores',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './autores.component.html',
  styleUrl: './autores.component.css'
})
export class AutoresComponent implements OnInit {

  autores: Autor[] = [];
  loading = false;
  editingId: number | null = null;
  error: string | null = null;

  pageSize = 10;
  page = 1;
  searchTerm = '';

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(40)]]
  });

  constructor(private fb: FormBuilder, private service: AutorService, private toast: ToastService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.error = null;
    this.service.search('').subscribe({
      next: (data) => {
        this.autores = data;
        this.page = 1;
      },
      error: () => this.toast.error('Falha ao carregar autores.'),
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
      this.toast.warning('Preencha o nome.');
      return;
    }
    const nome = this.form.getRawValue().nome.trim();

    this.loading = true;
    this.error = null;

    const req$ = this.editingId
      ? this.service.update(this.editingId, nome)
      : this.service.create(nome);

    req$.subscribe({
      next: () => {
        this.service.clearCache();
        this.toast.success(this.editingId ? 'Autor atualizado.' : 'Autor criado.');
        this.cancel();
        this.load();
      },
      complete: () => this.loading = false
    });
    this.loading = false
  }

  remove(a: Autor): void {
    if (!confirm(`Remover autor "${a.nome}"?`)) return;
    this.loading = true;
    this.error = null;
    this.service.delete(a.id).subscribe({
      next: () => {
        this.service.clearCache();
        this.toast.success('Autor removido.');
        this.load();
      },
      complete: () => this.loading = false
    });
    this.loading = false
  }

  get filteredAutores(): Autor[] {
    const term = (this.searchTerm || '').trim().toLowerCase();
    if (!term) return this.autores;
    return this.autores.filter(a => (a.nome || '').toLowerCase().includes(term));
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredAutores.length / this.pageSize));
  }

  get pagedAutores(): Autor[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredAutores.slice(start, start + this.pageSize);
  }

  goToPage(p: number): void {
    this.page = Math.min(Math.max(1, p), this.totalPages);
  }
}
