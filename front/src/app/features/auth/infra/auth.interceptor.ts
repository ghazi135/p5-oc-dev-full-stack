import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthFacade } from '../state/auth.facade';

/** Évite la boucle : 401 → refresh → retry → 401 (on ne retente qu'une fois). */
export const AUTH_REFRESH_ATTEMPTED = new HttpContextToken<boolean>(() => false);

/**
 * Sur 401 : tente un refresh puis rejoue la requête. Si le refresh échoue → /login.
 * On ignore les 401 sur /api/auth/refresh et les requêtes déjà retentées.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const facade = inject(AuthFacade);
  const router = inject(Router);

  return next(req).pipe(
    catchError((err: unknown) => {
      const is401 = err instanceof HttpErrorResponse && err.status === 401;
      if (!is401 || req.url.endsWith('/api/auth/refresh') || req.context.get(AUTH_REFRESH_ATTEMPTED)) {
        return throwError(() => err);
      }

      return facade.tryRefresh().pipe(
        switchMap((ok) => {
          if (!ok) {
            router.navigateByUrl('/login').catch(() => undefined);
            return throwError(() => err);
          }
          return next(req.clone({ context: req.context.set(AUTH_REFRESH_ATTEMPTED, true) }));
        })
      );
    })
  );
};
