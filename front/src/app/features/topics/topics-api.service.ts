import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type { CreatedIdResponse, TopicListItem } from './topic.models';

@Injectable({ providedIn: 'root' })
export class TopicsApiService {
  private readonly http = inject(HttpClient);

  listTopics(): Observable<TopicListItem[]> {
    return this.http.get<TopicListItem[]>('/api/topics', { withCredentials: true });
  }

  subscribeToTopic(topicId: number): Observable<CreatedIdResponse> {
    return this.http.post<CreatedIdResponse>(
      '/api/users/me/subscriptions',
      { topicId },
      { withCredentials: true }
    );
  }
}
