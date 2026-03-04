import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API } from './api';
import { Assunto } from '../models/assunto';
import { Observable, of } from 'rxjs';
import { catchError, shareReplay, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AssuntoService {
  constructor(private http: HttpClient) {}

  list(): Observable<Assunto[]> { return this.http.get<Assunto[]>(`${API}/assuntos`); }

  private cacheAll: Assunto[] | null = null;
  private cacheByTerm = new Map<string, Observable<Assunto[]>>();

  search(term: string): Observable<Assunto[]> {
    const q = (term || '').trim();
    const key = q.toLowerCase();
    if (this.cacheByTerm.has(key)) return this.cacheByTerm.get(key)!;

    const req$ = this.http
      .get<Assunto[]>(`${API}/assuntos`, { params: q ? { q } as any : {} as any })
      .pipe(
        catchError(() => {
          if (!this.cacheAll) return of([] as Assunto[]);
          if (!q) return of(this.cacheAll);
          const needle = key;
          return of(this.cacheAll.filter(a => (a.descricao || '').toLowerCase().includes(needle)));
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
  create(descricao: string): Observable<Assunto> { return this.http.post<Assunto>(`${API}/assuntos`, { descricao }); }
  update(id: number, descricao: string): Observable<Assunto> { return this.http.put<Assunto>(`${API}/assuntos/${id}`, { descricao }); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API}/assuntos/${id}`); }
}
