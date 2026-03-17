import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type {
  UpdateMeRequest,
  UpdatedResponse,
  UserMeResponse,
} from './models/user-me.models';

@Injectable({ providedIn: 'root' })
export class UserMeApiService {
  private readonly http = inject(HttpClient);

  me(): Observable<UserMeResponse> {
    return this.http.get<UserMeResponse>('/api/users/me', { withCredentials: true });
  }

  updateMe(payload: UpdateMeRequest): Observable<UpdatedResponse> {
    return this.http.put<UpdatedResponse>('/api/users/me', payload, { withCredentials: true });
  }

  unsubscribeFromTopic(topicId: number): Observable<void> {
    return this.http.delete<void>(`/api/users/me/subscriptions/${topicId}`, {
      withCredentials: true,
    });
  }
}
