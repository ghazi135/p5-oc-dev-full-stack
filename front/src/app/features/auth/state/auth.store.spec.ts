import { TestBed } from '@angular/core/testing';
import { AuthStore } from './auth.store';

describe('AuthStore', () => {
  let store: AuthStore;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(AuthStore);
  });

  it('should start unauthenticated with initialized=false', () => {
    expect(store.isAuthenticated()).toBeFalse();
    expect(store.initialized()).toBeFalse();
  });

  it('setAuthenticated should update isAuthenticated', () => {
    expect(store.isAuthenticated()).toBeFalse();
    store.setAuthenticated(true);
    expect(store.isAuthenticated()).toBeTrue();
  });

  it('clear should set unauthenticated', () => {
    store.setAuthenticated(true);
    store.clear();
    expect(store.isAuthenticated()).toBeFalse();
  });

  it('markInitialized should set initialized=true', () => {
    // Arrange
    expect(store.initialized()).toBeFalse();

    // Act
    store.markInitialized();

    // Assert
    expect(store.initialized()).toBeTrue();
  });
});
