import { Autor } from './autor';
import { Assunto } from './assunto';

export interface Livro {
  id: number;
  titulo: string;
  editora: string;
  edicao: number;
  anoPublicacao: number;
  valor: number;
  autores: Autor[];
  assuntos: Assunto[];
}

export interface LivroRequest {
  titulo: string;
  editora: string;
  edicao: number;
  anoPublicacao: number;
  valor: number;
  autoresIds: number[];
  assuntosIds: number[];
}
