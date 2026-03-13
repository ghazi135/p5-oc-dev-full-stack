import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { FeedItem, FeedOrder } from './feed.models';

@Injectable({ providedIn: 'root' })
export class FeedApiService {
  private readonly http = inject(HttpClient);

  /** GET /api/feed?order=desc|asc 🔒 */
  listFeed(order: FeedOrder): Observable<FeedItem[]> {
    const params = new HttpParams().set('order', order);
    return this.http.get<FeedItem[]>('/api/feed', {
      withCredentials: true,
      params,
    });
  }
}
