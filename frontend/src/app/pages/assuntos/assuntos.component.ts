import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssuntoService } from '../../services/assunto.service';
import { Assunto } from '../../models/assunto';

@Component({
  selector: 'app-assuntos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './assuntos.component.html',
  styleUrl: './assuntos.component.css'
})
export class AssuntosComponent implements OnInit {

  assuntos: Assunto[] = [];
  loading = false;
  editingId: number | null = null;
  error: string | null = null;

  form = this.fb.group({
    descricao: ['', [Validators.required, Validators.maxLength(20)]]
  });

  constructor(private fb: FormBuilder, private service: AssuntoService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.service.list().subscribe({
      next: (data) => this.assuntos = data,
      error: () => this.error = 'Falha ao carregar assuntos.',
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
      return;
    }
    const descricao = this.form.value.descricao!.trim();

    this.loading = true;
    this.error = null;

    const req$ = this.editingId
      ? this.service.update(this.editingId, descricao)
      : this.service.create(descricao);

    req$.subscribe({
      next: () => { this.cancel(); this.load(); },
      error: () => this.error = 'Falha ao salvar assunto.',
      complete: () => this.loading = false
    });
  }

  remove(a: Assunto): void {
    if (!confirm(`Remover assunto "${a.descricao}"?`)) return;
    this.loading = true;
    this.error = null;
    this.service.delete(a.id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Falha ao remover assunto (pode estar vinculado a livro).',
      complete: () => this.loading = false
    });
  }
}
