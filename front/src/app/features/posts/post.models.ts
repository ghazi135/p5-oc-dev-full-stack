export interface CreatedIdResponse {
  id: number;
}

export interface TopicSummary {
  id: number;
  name: string;
}

export interface UserSummary {
  id: number;
  username: string;
}

export interface CreatePostRequest {
  topicId: number;
  title: string;
  content: string;
}

export interface CreateCommentRequest {
  content: string;
}

export interface PostCommentDto {
  id: number;
  content: string;
  author: UserSummary;
  createdAt: string;
}

export interface PostDetailResponse {
  id: number;
  topic: TopicSummary;
  title: string;
  content: string;
  author: UserSummary;
  createdAt: string;
  comments: PostCommentDto[];
}
