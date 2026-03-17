import { AsyncPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  distinctUntilChanged,
  map,
  of,
  shareReplay,
  startWith,
  switchMap,
  catchError,
} from 'rxjs';

import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';

import { FeedApiService } from './feed-api.service';
import { FeedItem, FeedOrder } from './models/feed.models';
import { parseOrder, formatComments } from './feed.utils';

type FeedVm = {
  loading: boolean;
  error: string | null;
  feed: FeedItem[];
  order: FeedOrder;
};

@Component({
  selector: 'mdd-feed',
  standalone: true,
  imports: [
    AsyncPipe,
    DatePipe,
    MatCardModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatMenuModule,
    MatIconModule,
    RouterLink,
  ],
  templateUrl: './feed.html',
  styleUrl: './feed.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Feed {
  private readonly api = inject(FeedApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly formatComments = formatComments;

  private readonly order$ = this.route.queryParamMap.pipe(
    map((pm) => parseOrder(pm.get('order'))),
    distinctUntilChanged(),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly vm$ = this.order$.pipe(
    switchMap((order) =>
      this.api.listFeed(order).pipe(
        map((feed) => ({ loading: false, error: null, feed, order } satisfies FeedVm)),
        startWith({ loading: true, error: null, feed: [], order } satisfies FeedVm),
        catchError(() =>
          of({
            loading: false,
            error: 'Impossible de charger le feed.',
            feed: [],
            order,
          } satisfies FeedVm)
        )
      )
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
    takeUntilDestroyed(this.destroyRef)
  );

  onOrderChange(order: FeedOrder): void {
    this.router
      .navigate([], {
        relativeTo: this.route,
        queryParams: { order },
      })
      .catch(() => undefined);
  }
}
