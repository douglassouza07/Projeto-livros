import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { API } from '../../services/api';

@Component({
  selector: 'app-relatorio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './relatorio.component.html',
  styleUrl: './relatorio.component.css'
})
export class RelatorioComponent {
  pdfUrl = `${API}/relatorios/livros-por-autor.pdf`;

  abrir(): void {
    window.open(this.pdfUrl, '_blank');
  }
}
