import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from './api';
import { Autor } from '../models/autor';
import { Observable, of } from 'rxjs';
import { catchError, shareReplay, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AutorService {
  constructor(private http: HttpClient) {}

  list(): Observable<Autor[]> { return this.http.get<Autor[]>(`${API}/autores`); }

  // autocomplete remoto com cache por termo
  private cacheAll: Autor[] | null = null;
  private cacheByTerm = new Map<string, Observable<Autor[]>>();

  search(term: string): Observable<Autor[]> {
    const q = (term || '').trim();
    const key = q.toLowerCase();
    if (this.cacheByTerm.has(key)) return this.cacheByTerm.get(key)!;

    const req$ = this.http
      .get<Autor[]>(`${API}/autores`, { params: q ? { q } as any : {} as any })
      .pipe(
        catchError(() => {
          // fallback: se o backend não filtra, usa cache local
          if (!this.cacheAll) return of([] as Autor[]);
          if (!q) return of(this.cacheAll);
          const needle = key;
          return of(this.cacheAll.filter(a => (a.nome || '').toLowerCase().includes(needle)));
        }),
        tap(list => {
          if (!q) this.cacheAll = list;
        }),
        shareReplay(1)
      );

    this.cacheByTerm.set(key, req$);
    return req$;
  }

  clearCache(): void {
    this.cacheAll = null;
    this.cacheByTerm.clear();
  }
  create(nome: string): Observable<Autor> { return this.http.post<Autor>(`${API}/autores`, { nome }); }
  update(id: number, nome: string): Observable<Autor> { return this.http.put<Autor>(`${API}/autores/${id}`, { nome }); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API}/autores/${id}`); }
}
