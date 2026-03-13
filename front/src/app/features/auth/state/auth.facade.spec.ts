import { TestBed } from '@angular/core/testing';
import { firstValueFrom, of, Subject, throwError } from 'rxjs';

import { AuthFacade } from './auth.facade';
import { AuthStore } from './auth.store';
import { AuthApiService } from '../services/auth-api.service';
import type { TokenResponse } from '../interfaces/auth.models';
import { buildValidSecret } from '@core/testing/test-secrets';

/**
 * Helper top-level pour créer une réponse TokenResponse.
 */
function tokenResponse(accessToken: string): TokenResponse {
  return { accessToken, tokenType: 'Bearer', expiresInSeconds: 900 };
}

describe('AuthFacade', () => {
  let facade: AuthFacade;
  let store: AuthStore;
  let api: jasmine.SpyObj<AuthApiService>;

  beforeEach(() => {
    // Arrange
    api = jasmine.createSpyObj<AuthApiService>('AuthApiService', [
      'csrf',
      'login',
      'register',
      'refresh',
      'logout',
    ]);

    TestBed.configureTestingModule({
      providers: [AuthFacade, AuthStore, { provide: AuthApiService, useValue: api }],
    });

    facade = TestBed.inject(AuthFacade);
    store = TestBed.inject(AuthStore);
  });

  it('login() should call csrf best-effort then set authenticated in store', (done) => {
    api.csrf.and.returnValue(of(void 0));
    api.login.and.returnValue(of(tokenResponse('jwt')));
    const secret = buildValidSecret();

    facade.login({ identifier: 'bob', password: secret }).subscribe({
      next: (res) => {
        expect(res.accessToken).toBe('jwt');
        expect(api.csrf).toHaveBeenCalled();
        expect(api.login).toHaveBeenCalled();
        expect(store.isAuthenticated()).toBeTrue();
        done();
      },
      error: done.fail,
    });
  });

  it('logout() should clear authenticated even when API fails (finalize)', () => {
    store.setAuthenticated(true);
    api.logout.and.returnValue(throwError(() => new Error('boom')));
    facade.logout().subscribe();
    expect(store.isAuthenticated()).toBeFalse();
  });

  it('refreshAccessTokenOnce() should deduplicate concurrent refresh calls', (done) => {
    const refresh$ = new Subject<TokenResponse>();
    api.refresh.and.returnValue(refresh$.asObservable());
    const results: boolean[] = [];

    facade.refreshAccessTokenOnce().subscribe((v) => results.push(v));
    facade.refreshAccessTokenOnce().subscribe((v) => results.push(v));

    expect(api.refresh).toHaveBeenCalledTimes(1);
    refresh$.next(tokenResponse('new-jwt'));
    refresh$.complete();

    setTimeout(() => {
      expect(results).toEqual([true, true]);
      expect(store.isAuthenticated()).toBeTrue();
      done();
    }, 0);
  });

  it('bootstrap$() should call csrf then refresh, set authenticated, and mark initialized', async () => {
    api.csrf.and.returnValue(of(void 0));
    api.refresh.and.returnValue(of(tokenResponse('boot-jwt')));
    spyOn(store, 'markInitialized').and.callThrough();

    await firstValueFrom(facade.bootstrap$());

    expect(api.csrf).toHaveBeenCalled();
    expect(api.refresh).toHaveBeenCalled();
    expect(store.isAuthenticated()).toBeTrue();
    expect(store.markInitialized).toHaveBeenCalled();
  });
});
