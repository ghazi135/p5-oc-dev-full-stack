import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import type {
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
  TokenResponse,
} from '../models/auth.models';

/** Appels HTTP /api/auth/* (withCredentials pour les cookies). */
@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);

  csrf(): Observable<void> {
    return this.http.get<void>('/api/auth/csrf', { withCredentials: true });
  }

  register(payload: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>('/api/auth/register', payload, {
      withCredentials: true,
    });
  }

  login(payload: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>('/api/auth/login', payload, {
      withCredentials: true,
    });
  }

  refresh(): Observable<TokenResponse> {
    return this.http.post<TokenResponse>('/api/auth/refresh', {}, { withCredentials: true });
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', {}, { withCredentials: true });
  }
}
