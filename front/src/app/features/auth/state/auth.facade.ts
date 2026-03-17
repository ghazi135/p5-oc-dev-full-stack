import { Injectable, inject } from '@angular/core';
import { Observable, catchError, finalize, firstValueFrom, map, of, shareReplay, switchMap, tap } from 'rxjs';

import type { LoginRequest, RegisterRequest, RegisterResponse, TokenResponse } from '../models/auth.models';
import { AuthApiService } from '../services/auth-api.service';
import { AuthStore } from './auth.store';

/** Orchestration auth : bootstrap au démarrage, login/logout, tryRefresh sur 401 (voir README auth). */
@Injectable({ providedIn: 'root' })
export class AuthFacade {
  private readonly api = inject(AuthApiService);
  private readonly store = inject(AuthStore);

  /** Un seul refresh à la fois (plusieurs 401 simultanés = une seule requête refresh). */
  private refreshInFlight$?: Observable<boolean>;

  /** Au démarrage : csrf puis refresh, puis marque initialized. */
  bootstrap(): Promise<void> {
    return firstValueFrom(this.bootstrap$());
  }

  bootstrap$(): Observable<void> {
    return this.api.csrf().pipe(
      catchError(() => of(void 0)),
      switchMap(() =>
        this.api.refresh().pipe(
          tap(() => this.store.setAuthenticated(true)),
          map(() => void 0),
          catchError(() => {
            this.store.setAuthenticated(false);
            return of(void 0);
          })
        )
      ),
      finalize(() => this.store.markInitialized())
    );
  }

  /** Login : csrf puis login, puis marque connecté. */
  login(payload: LoginRequest): Observable<TokenResponse> {
    return this.api.csrf().pipe(
      catchError(() => of(void 0)),
      switchMap(() => this.api.login(payload)),
      tap(() => this.store.setAuthenticated(true))
    );
  }

  /** Inscription (sans connexion auto). */
  register(payload: RegisterRequest): Observable<RegisterResponse> {
    return this.api.register(payload);
  }

  /** Déconnexion : appelle l’API puis vide le store. */
  logout(): Observable<void> {
    return this.api.logout().pipe(
      catchError(() => of(void 0)),
      finalize(() => this.store.clear())
    );
  }

  /** Utilisé par l'interceptor sur 401 : un seul refresh en cours, puis retry. */
  tryRefresh(): Observable<boolean> {
    if (this.refreshInFlight$) return this.refreshInFlight$;

    const refresh$ = this.api.refresh().pipe(
      tap(() => this.store.setAuthenticated(true)),
      map(() => true),
      catchError(() => {
        this.store.setAuthenticated(false);
        return of(false);
      }),
      finalize(() => {
        this.refreshInFlight$ = undefined;
      }),
      shareReplay({ bufferSize: 1, refCount: false })
    );

    this.refreshInFlight$ = refresh$;
    return refresh$;
  }
}
