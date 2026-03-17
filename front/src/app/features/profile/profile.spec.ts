import { HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Profile } from './profile';
import { DEFAULT_COMPONENT_TEST_PROVIDERS } from '@core/testing/test.providers';

type MeDto = {
  id: number;
  email: string;
  username: string;
  subscriptions: Array<{ id: number; name: string }>;
};

type TopicDto = {
  id: number;
  name: string;
  description: string;
  subscribed: boolean;
};

function flushInitRequests(httpMock: HttpTestingController, me: MeDto, topics: TopicDto[]): void {
  const meReq = httpMock.expectOne('/api/users/me');
  expect(meReq.request.method).toBe('GET');
  meReq.flush(me);

  const topicsReq = httpMock.expectOne('/api/topics');
  expect(topicsReq.request.method).toBe('GET');
  topicsReq.flush(topics);
}

function flushReloadMe(httpMock: HttpTestingController, me: MeDto): void {
  const meReq = httpMock.expectOne('/api/users/me');
  expect(meReq.request.method).toBe('GET');
  meReq.flush(me);
}

function flushReloadTopics(httpMock: HttpTestingController, topics: TopicDto[]): void {
  const topicsReq = httpMock.expectOne('/api/topics');
  expect(topicsReq.request.method).toBe('GET');
  topicsReq.flush(topics);
}

function getComponentSnackBar(component: Profile): MatSnackBar {
  return (component as unknown as { snackBar: MatSnackBar }).snackBar;
}

describe('Profile', () => {
  let fixture: ComponentFixture<Profile>;
  let component: Profile;
  let httpMock: HttpTestingController;
  let snackBar: MatSnackBar;
  let openSpy: jasmine.Spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [...DEFAULT_COMPONENT_TEST_PROVIDERS],
    }).compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    snackBar = getComponentSnackBar(component);
    openSpy = spyOn(snackBar, 'open').and.stub();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should load me + topics on init (success)', () => {
    const me: MeDto = {
      id: 1,
      email: 'user@mail.com',
      username: 'devUser',
      subscriptions: [{ id: 10, name: 'Java' }],
    };
    const topics: TopicDto[] = [
      { id: 10, name: 'Java', description: 'desc', subscribed: true },
      { id: 11, name: 'Angular', description: 'desc', subscribed: false },
    ];

    fixture.detectChanges();
    flushInitRequests(httpMock, me, topics);
    fixture.detectChanges();

    expect(component.me()?.email).toBe('user@mail.com');
    expect(component.initialMe()?.username).toBe('devUser');
    expect(component.subscribedTopics().map((t) => t.id)).toEqual([10]);
    expect(openSpy).not.toHaveBeenCalled();
  });

  it('submit() should PUT update and then reload me (success path)', () => {
    fixture.detectChanges();
    flushInitRequests(
      httpMock,
      { id: 1, email: 'user@mail.com', username: 'devUser', subscriptions: [] },
      []
    );
    fixture.detectChanges();

    component.form.patchValue({
      email: 'user@mail.com',
      username: 'newUser',
      password: '',
    });
    component.form.updateValueAndValidity();
    expect(component.form.valid).toBeTrue();

    component.submit();

    const putReq = httpMock.expectOne('/api/users/me');
    expect(putReq.request.method).toBe('PUT');
    expect(putReq.request.body).toEqual({
      email: 'user@mail.com',
      username: 'newUser',
      password: null,
    });

    putReq.flush({ updated: true });
    fixture.detectChanges();

    expect(openSpy).toHaveBeenCalledWith('Profil mis à jour.', 'OK', { duration: 2000 });

    flushReloadMe(httpMock, {
      id: 1,
      email: 'user@mail.com',
      username: 'newUser',
      subscriptions: [],
    });
    fixture.detectChanges();

    expect(component.me()?.username).toBe('newUser');
  });

  it('unsubscribe() should optimistically update then call DELETE and reload topics on success', () => {
    fixture.detectChanges();
    flushInitRequests(
      httpMock,
      { id: 1, email: 'user@mail.com', username: 'devUser', subscriptions: [] },
      [{ id: 10, name: 'Java', description: 'desc', subscribed: true }]
    );
    fixture.detectChanges();

    expect(component.topics()?.[0].subscribed).toBeTrue();

    component.unsubscribe(10);

    expect(component.topics()?.[0].subscribed).toBeFalse();

    const delReq = httpMock.expectOne('/api/users/me/subscriptions/10');
    expect(delReq.request.method).toBe('DELETE');

    delReq.flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();

    expect(openSpy).toHaveBeenCalledWith('Abonnement supprimé.', 'OK', { duration: 2000 });

    flushReloadTopics(httpMock, [{ id: 10, name: 'Java', description: 'desc', subscribed: false }]);
    fixture.detectChanges();

    expect(component.topics()?.[0].subscribed).toBeFalse();
  });

  it('unsubscribe() should rollback optimistic update on ApiErrorResponse', () => {
    fixture.detectChanges();
    flushInitRequests(
      httpMock,
      { id: 1, email: 'user@mail.com', username: 'devUser', subscriptions: [] },
      [{ id: 10, name: 'Java', description: 'desc', subscribed: true }]
    );
    fixture.detectChanges();

    component.unsubscribe(10);

    expect(component.topics()?.[0].subscribed).toBeFalse();

    const delReq = httpMock.expectOne('/api/users/me/subscriptions/10');
    delReq.flush(
      { error: 'CONFLICT', message: 'Déjà désabonné', fieldErrors: [] },
      { status: 409, statusText: 'Conflict' }
    );
    fixture.detectChanges();

    expect(component.topics()?.[0].subscribed).toBeTrue();
    expect(openSpy).toHaveBeenCalledWith('Déjà désabonné', 'OK', { duration: 3000 });

    httpMock.expectNone('/api/topics');
  });

  it('unsubscribe() should ignore call when topicId is already pending (double-click guard)', () => {
    fixture.detectChanges();
    flushInitRequests(
      httpMock,
      { id: 1, email: 'user@mail.com', username: 'devUser', subscriptions: [] },
      [{ id: 10, name: 'Java', description: 'desc', subscribed: true }]
    );
    fixture.detectChanges();

    component.unsubPendingIds.set(new Set([10]));
    expect(component.unsubPendingIds().has(10)).toBeTrue();

    component.unsubscribe(10);

    httpMock.expectNone('/api/users/me/subscriptions/10');
    expect(openSpy).not.toHaveBeenCalled();
  });

  it('loadTopics() should set topicsError and open snackBar on error', () => {
    fixture.detectChanges();

    const meReq = httpMock.expectOne('/api/users/me');
    meReq.flush({ id: 1, email: 'user@mail.com', username: 'devUser', subscriptions: [] });

    const topicsReq = httpMock.expectOne('/api/topics');
    topicsReq.flush({}, { status: 500, statusText: 'Internal Server Error' });
    fixture.detectChanges();

    expect(component.topicsError()).toBe('Impossible de charger vos abonnements.');
    expect(openSpy).toHaveBeenCalledWith('Impossible de charger vos abonnements.', 'OK', {
      duration: 3000,
    });
  });
});
