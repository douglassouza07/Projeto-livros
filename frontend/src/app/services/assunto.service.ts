import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from './api';
import { Assunto } from '../models/assunto';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AssuntoService {
  constructor(private http: HttpClient) {}

  list(): Observable<Assunto[]> { return this.http.get<Assunto[]>(`${API}/assuntos`); }
  create(descricao: string): Observable<Assunto> { return this.http.post<Assunto>(`${API}/assuntos`, { descricao }); }
  update(id: number, descricao: string): Observable<Assunto> { return this.http.put<Assunto>(`${API}/assuntos/${id}`, { descricao }); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API}/assuntos/${id}`); }
}
