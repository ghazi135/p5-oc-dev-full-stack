import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { FeedApiService } from './feed-api.service';

describe('FeedApiService', () => {
  let service: FeedApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(FeedApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('listFeed(order) should GET /api/feed?order=desc withCredentials=true', () => {
    service.listFeed('desc').subscribe((items) => {
      expect(items.length).toBe(1);
      expect(items[0].id).toBe(1);
    });

    const req = httpMock.expectOne((r) => r.url === '/api/feed' && r.params.get('order') === 'desc');
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush([{ id: 1, title: 't', content: 'c', createdAt: '2025-01-01', author: { id: 1, username: 'u' }, topic: { id: 1, name: 'Java' }, commentsCount: 0 }]);
  });
});
