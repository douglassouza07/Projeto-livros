import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from './api';
import { Livro, LivroRequest } from '../models/livro';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LivroService {
  constructor(private http: HttpClient) {}

  list(): Observable<Livro[]> { return this.http.get<Livro[]>(`${API}/livros`); }
  create(req: LivroRequest): Observable<Livro> { return this.http.post<Livro>(`${API}/livros`, req); }
  update(id: number, req: LivroRequest): Observable<Livro> { return this.http.put<Livro>(`${API}/livros/${id}`, req); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API}/livros/${id}`); }
}
