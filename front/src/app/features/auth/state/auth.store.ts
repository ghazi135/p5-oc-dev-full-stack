import { Injectable, signal } from '@angular/core';

/**
 * Store minimal d'authentification.
 *
 * Le JWT est géré par le backend (cookie HttpOnly accessToken).
 * Le frontend ne stocke que l'état "connecté" (login/refresh succès).
 */
@Injectable({ providedIn: 'root' })
export class AuthStore {
  /** Utilisateur connecté (true après login ou refresh réussi). */
  private readonly _isAuthenticated = signal(false);
  readonly isAuthenticated = this._isAuthenticated.asReadonly();

  /** Indique que le bootstrap SPA (csrf -> refresh) a été exécuté. */
  private readonly _initialized = signal(false);
  readonly initialized = this._initialized.asReadonly();

  setAuthenticated(value: boolean): void {
    this._isAuthenticated.set(value);
  }

  /** Déconnexion : purge l'état local (les cookies sont supprimés par le backend). */
  clear(): void {
    this._isAuthenticated.set(false);
  }

  markInitialized(): void {
    this._initialized.set(true);
  }
}
