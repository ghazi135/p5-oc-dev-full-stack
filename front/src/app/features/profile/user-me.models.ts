export interface TopicDto {
  id: number;
  name: string;
}

export interface UserMeResponse {
  id: number;
  email: string;
  username: string;
  subscriptions: TopicDto[];
}

export interface UpdateMeRequest {
  email?: string | null;
  username?: string | null;
  password?: string | null;
}

export interface UpdatedResponse {
  updated: boolean;
}
