import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { Feed } from './feed';
import type { FeedOrder } from './feed.models';

function createFeedTestBed(orderParam: string | null) {
  const router = jasmine.createSpyObj<Router>('Router', ['navigate']);
  router.navigate.and.resolveTo(true);

  TestBed.configureTestingModule({
    imports: [Feed],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      { provide: Router, useValue: router },
      {
        provide: ActivatedRoute,
        useValue: { queryParamMap: of(convertToParamMap(orderParam ? { order: orderParam } : {})) },
      },
    ],
  });

  const fixture = TestBed.createComponent(Feed);
  const component = fixture.componentInstance;
  const httpMock = TestBed.inject(HttpTestingController);

  return { fixture, component, httpMock, router };
}

describe('Feed', () => {
  afterEach(() => {
    TestBed.inject(HttpTestingController).verify();
  });

  it('vm$ should call GET /api/feed with default order=desc when no query param', () => {
    const { component, httpMock } = createFeedTestBed(null);
    const emissions: any[] = [];
    const sub = component.vm$.subscribe((v) => emissions.push(v));

    const req = httpMock.expectOne(
      (r) => r.url === '/api/feed' && r.params.get('order') === 'desc'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush([]);

    const last = emissions.at(-1);
    expect(last.loading).toBeFalse();
    expect(last.error).toBeNull();
    expect(last.order).toBe('desc');
    sub.unsubscribe();
  });

  it('vm$ should call GET /api/feed with order=asc when query param order=asc', () => {
    const { component, httpMock } = createFeedTestBed('asc');
    const emissions: any[] = [];
    const sub = component.vm$.subscribe((v) => emissions.push(v));

    const req = httpMock.expectOne((r) => r.url === '/api/feed' && r.params.get('order') === 'asc');
    expect(req.request.method).toBe('GET');
    req.flush([]);

    const last = emissions.at(-1);
    expect(last.loading).toBeFalse();
    expect(last.error).toBeNull();
    expect(last.order).toBe('asc');
    sub.unsubscribe();
  });

  it('vm$ should expose error message when API fails (catchError branch)', () => {
    const { component, httpMock } = createFeedTestBed('desc');
    const emissions: any[] = [];
    const sub = component.vm$.subscribe((v) => emissions.push(v));

    const req = httpMock.expectOne(
      (r) => r.url === '/api/feed' && r.params.get('order') === 'desc'
    );
    req.flush({}, { status: 500, statusText: 'Server Error' });

    const last = emissions.at(-1);
    expect(last.loading).toBeFalse();
    expect(last.error).toBe('Impossible de charger le feed.');
    expect(last.feed).toEqual([]);
    sub.unsubscribe();
  });

  it('onOrderChange() should call router.navigate with queryParams order', () => {
    const { component, router } = createFeedTestBed('desc');
    component.onOrderChange('asc' as FeedOrder);

    expect(router.navigate).toHaveBeenCalled();
    const args = router.navigate.calls.mostRecent().args;
    expect(args[0]).toEqual([]);
    expect(args[1]?.queryParams).toEqual({ order: 'asc' });
  });
});
