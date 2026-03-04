import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssuntoService } from '../../services/assunto.service';
import { Assunto } from '../../models/assunto';
import { ToastService } from '../../core/toast/toast.service';

@Component({
  selector: 'app-assuntos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './assuntos.component.html',
  styleUrl: './assuntos.component.css'
})
export class AssuntosComponent implements OnInit {

  assuntos: Assunto[] = [];
  loading = false;
  editingId: number | null = null;
  error: string | null = null;

  pageSize = 10;
  page = 1;
  searchTerm = '';

  form = this.fb.nonNullable.group({
    descricao: ['', [Validators.required, Validators.maxLength(20)]]
  });

  constructor(private fb: FormBuilder, private service: AssuntoService, private toast: ToastService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.service.search('').subscribe({
      next: (data) => {
        this.assuntos = data;
        this.page = 1;
      },
      error: () => this.toast.error('Falha ao carregar assuntos.'),
      complete: () => this.loading = false
    });
  }

  startEdit(a: Assunto): void {
    this.editingId = a.id;
    this.form.setValue({ descricao: a.descricao });
  }

  cancel(): void {
    this.editingId = null;
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.warning('Preencha a descrição.');
      return;
    }
    const descricao = this.form.getRawValue().descricao.trim();

    this.loading = true;
    this.error = null;

    const req$ = this.editingId
      ? this.service.update(this.editingId, descricao)
      : this.service.create(descricao);

    req$.subscribe({
      next: () => {
        this.service.clearCache();
        this.toast.success(this.editingId ? 'Assunto atualizado.' : 'Assunto criado.');
        this.cancel();
        this.load();
      },
      complete: () => this.loading = false
    });
    this.loading = false
  }

  remove(a: Assunto): void {
    if (!confirm(`Remover assunto "${a.descricao}"?`)) return;
    this.loading = true;
    this.error = null;
    this.service.delete(a.id).subscribe({
      next: () => {
        this.service.clearCache();
        this.toast.success('Assunto removido.');
        this.load();
      },
      complete: () => this.loading = false
    });
    this.loading = false
  }

  get filteredAssuntos(): Assunto[] {
    const term = (this.searchTerm || '').trim().toLowerCase();
    if (!term) return this.assuntos;
    return this.assuntos.filter(a => (a.descricao || '').toLowerCase().includes(term));
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredAssuntos.length / this.pageSize));
  }

  get pagedAssuntos(): Assunto[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredAssuntos.slice(start, start + this.pageSize);
  }

  goToPage(p: number): void {
    this.page = Math.min(Math.max(1, p), this.totalPages);
  }
}
