import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, Router } from '@angular/router';
import { filter, map, take } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

import { AuthStore } from '../state/auth.store';

/** Attend que le bootstrap auth soit terminé (voir README auth). */
function afterAuthInit$(store: AuthStore) {
  return toObservable(store.initialized).pipe(filter(Boolean), take(1));
}

/** Route protégée : connecté → OK, sinon → /login */
const protectedRoute$ = () => {
  const store = inject(AuthStore);
  const router = inject(Router);
  return afterAuthInit$(store).pipe(
    map(() => (store.isAuthenticated() ? true : router.parseUrl('/login')))
  );
};

export const authGuard: CanActivateFn = () => protectedRoute$();
export const authChildGuard: CanActivateChildFn = () => protectedRoute$();

/** Route publique (login, register) : non connecté → OK, déjà connecté → /feed */
export const publicOnlyGuard: CanActivateFn = () => {
  const store = inject(AuthStore);
  const router = inject(Router);
  return afterAuthInit$(store).pipe(
    map(() => (!store.isAuthenticated() ? true : router.parseUrl('/feed')))
  );
};
