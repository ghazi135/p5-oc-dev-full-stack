import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthFacade } from '../state/auth.facade';
import { AUTH_REFRESH_ATTEMPTED } from './auth.http-context';

/**
 * Interceptor Auth :
 * - Le JWT est envoyé via cookie HttpOnly (accessToken) ; pas de header Authorization.
 * - Sur 401 : une tentative de refresh (cookie refreshToken) puis rejouer la requête.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const facade = inject(AuthFacade);
  const router = inject(Router);

  const isRefresh = req.url.endsWith('/api/auth/refresh');

  return next(req).pipe(
    catchError((err: unknown) => {
      if (!(err instanceof HttpErrorResponse) || err.status !== 401) {
        return throwError(() => err);
      }
      if (isRefresh) {
        return throwError(() => err);
      }
      if (req.context.get(AUTH_REFRESH_ATTEMPTED)) {
        return throwError(() => err);
      }

      return facade.refreshAccessTokenOnce().pipe(
        switchMap((success) => {
          if (!success) {
            router.navigateByUrl('/login').catch(() => undefined);
            return throwError(() => err);
          }
          const retryReq = req.clone({
            context: req.context.set(AUTH_REFRESH_ATTEMPTED, true),
          });
          return next(retryReq);
        })
      );
    })
  );
};
