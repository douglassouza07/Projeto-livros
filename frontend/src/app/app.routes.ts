import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { AutoresComponent } from './pages/autores/autores.component';
import { AssuntosComponent } from './pages/assuntos/assuntos.component';
import { LivrosComponent } from './pages/livros/livros.component';
import { RelatorioComponent } from './pages/relatorio/relatorio.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'autores', component: AutoresComponent },
  { path: 'assuntos', component: AssuntosComponent },
  { path: 'livros', component: LivrosComponent },
  { path: 'relatorio', component: RelatorioComponent },
  { path: '**', redirectTo: '' }
];
