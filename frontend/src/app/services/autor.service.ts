import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from './api';
import { Autor } from '../models/autor';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AutorService {
  constructor(private http: HttpClient) {}

  list(): Observable<Autor[]> { return this.http.get<Autor[]>(`${API}/autores`); }
  create(nome: string): Observable<Autor> { return this.http.post<Autor>(`${API}/autores`, { nome }); }
  update(id: number, nome: string): Observable<Autor> { return this.http.put<Autor>(`${API}/autores/${id}`, { nome }); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API}/autores/${id}`); }
}
