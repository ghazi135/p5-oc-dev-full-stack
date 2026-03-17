import { Injectable, signal } from '@angular/core';

/** État auth côté front : connecté oui/non + bootstrap terminé (voir README auth). */
@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly _isAuthenticated = signal(false);
  readonly isAuthenticated = this._isAuthenticated.asReadonly();

  private readonly _initialized = signal(false);
  readonly initialized = this._initialized.asReadonly();

  setAuthenticated(value: boolean): void {
    this._isAuthenticated.set(value);
  }

  clear(): void {
    this._isAuthenticated.set(false);
  }

  markInitialized(): void {
    this._initialized.set(true);
  }
}
