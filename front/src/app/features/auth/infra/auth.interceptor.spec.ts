import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpContext, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { authInterceptor, AUTH_REFRESH_ATTEMPTED } from './auth.interceptor';
import { AuthStore } from '../state/auth.store';
import { AuthFacade } from '../state/auth.facade';

/**
 * Factory : HttpClient avec interceptor, Router et AuthFacade spies.
 * refreshResult = résultat de tryRefresh() (true = OK, false → redirect login).
 */
function setupInterceptorTestBed(refreshResult: boolean) {
  const router = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);
  router.navigateByUrl.and.resolveTo(true);

  const facade = jasmine.createSpyObj<AuthFacade>('AuthFacade', ['tryRefresh']);
  facade.tryRefresh.and.returnValue(of(refreshResult));

  TestBed.configureTestingModule({
    providers: [
      { provide: Router, useValue: router },
      { provide: AuthFacade, useValue: facade },
      AuthStore,
      provideHttpClient(withInterceptors([authInterceptor])),
      provideHttpClientTesting(),
    ],
  });

  const http = TestBed.inject(HttpClient);
  const httpMock = TestBed.inject(HttpTestingController);
  const store = TestBed.inject(AuthStore);

  store.markInitialized();

  return { http, httpMock, store, router, facade };
}

describe('authInterceptor', () => {
  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
  });

  it('should NOT add Authorization header (JWT is sent via cookie)', () => {
    const { http, httpMock } = setupInterceptorTestBed(true);
    http.get('/api/feed').subscribe();
    const req = httpMock.expectOne('/api/feed');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });

  it('on 401, should refresh once then retry and mark AUTH_REFRESH_ATTEMPTED', (done) => {
    const { http, httpMock, facade } = setupInterceptorTestBed(true);

    http.get('/api/feed').subscribe({
      next: () => done(),
      error: done.fail,
    });

    const first = httpMock.expectOne('/api/feed');
    first.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(facade.tryRefresh).toHaveBeenCalledTimes(1);

    const retry = httpMock.expectOne('/api/feed');
    expect(retry.request.context.get(AUTH_REFRESH_ATTEMPTED)).toBeTrue();
    retry.flush([]);
  });

  it('should NOT refresh if AUTH_REFRESH_ATTEMPTED is already true', (done) => {
    const { http, httpMock, facade } = setupInterceptorTestBed(true);

    const ctx = new HttpContext().set(AUTH_REFRESH_ATTEMPTED, true);

    // Act
    http.get('/api/feed', { context: ctx }).subscribe({
      next: () => done.fail('Expected an error'),
      error: (err) => {
        // Assert
        expect(err.status).toBe(401);
        expect(facade.tryRefresh).not.toHaveBeenCalled();
        done();
      },
    });

    // Assert : single request, no refresh
    const req = httpMock.expectOne('/api/feed');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('should NOT attempt refresh on /api/auth/refresh itself (avoid loop)', (done) => {
    const { http, httpMock, facade } = setupInterceptorTestBed(true);

    // Act
    http.post('/api/auth/refresh', {}).subscribe({
      next: () => done.fail('Expected an error'),
      error: (err) => {
        // Assert
        expect(err.status).toBe(401);
        expect(facade.tryRefresh).not.toHaveBeenCalled();
        done();
      },
    });

    // Assert
    const req = httpMock.expectOne('/api/auth/refresh');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('when refresh returns false, should redirect to /login and propagate error', (done) => {
    const { http, httpMock, router } = setupInterceptorTestBed(false);

    http.get('/api/feed').subscribe({
      next: () => done.fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(401);
        expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
        done();
      },
    });

    const first = httpMock.expectOne('/api/feed');
    first.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
