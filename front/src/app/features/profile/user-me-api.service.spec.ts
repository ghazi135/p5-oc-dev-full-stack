import { TestBed } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';

import { UserMeApiService } from './user-me-api.service';
import { HTTP_TEST_PROVIDERS } from '@core/testing/test.providers';

describe('UserMeApiService', () => {
  let api: UserMeApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [...HTTP_TEST_PROVIDERS],
    });
    api = TestBed.inject(UserMeApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call GET /api/users/me with credentials', () => {
    api.me().subscribe();

    const req = httpMock.expectOne('/api/users/me');
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ id: 1, email: 'a@b.com', username: 'u', subscriptions: [] });
  });

  it('should call PUT /api/users/me with credentials', () => {
    api.updateMe({ username: 'new' }).subscribe();

    const req = httpMock.expectOne('/api/users/me');
    expect(req.request.method).toBe('PUT');
    expect(req.request.withCredentials).toBeTrue();
    expect(req.request.body).toEqual({ username: 'new' });
    req.flush({ updated: true });
  });

  it('should call DELETE /api/users/me/subscriptions/:topicId with credentials', () => {
    api.unsubscribeFromTopic(11).subscribe();

    const req = httpMock.expectOne('/api/users/me/subscriptions/11');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(null);
  });
});
