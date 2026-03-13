package com.openclassrooms.mdd_api.post.service;

import com.openclassrooms.mdd_api.comment.entity.Comment;
import com.openclassrooms.mdd_api.comment.repository.CommentRepository;
import com.openclassrooms.mdd_api.common.web.exception.ApiNotFoundException;
import com.openclassrooms.mdd_api.post.dto.*;
import com.openclassrooms.mdd_api.post.entity.Post;
import com.openclassrooms.mdd_api.post.repository.PostRepository;
import com.openclassrooms.mdd_api.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mdd_api.topic.entity.Topic;
import com.openclassrooms.mdd_api.topic.repository.TopicRepository;
import com.openclassrooms.mdd_api.user.entity.User;
import com.openclassrooms.mdd_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String FORBIDDEN = "Forbidden";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public Long createPost(Long userId, CreatePostRequest req) {
        Topic topic = topicRepository.findById(req.topicId())
                .orElseThrow(() -> new ApiNotFoundException("Topic not found"));
        boolean subscribed = subscriptionRepository.existsByUser_IdAndTopic_Id(userId, topic.getId());
        if (!subscribed) throw new AccessDeniedException(FORBIDDEN);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ApiNotFoundException("User not found"));
        Post post = new Post(req.title(), req.content(), topic, author);
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findDetailById(postId)
                .orElseThrow(() -> new ApiNotFoundException("Post not found"));
        List<Comment> comments = commentRepository.findByPost_IdOrderByCreatedAtDescIdDesc(postId);
        PostTopicDto topicDto = new PostTopicDto(post.getTopic().getId(), post.getTopic().getName());
        PostAuthorDto authorDto = new PostAuthorDto(post.getAuthor().getId(), post.getAuthor().getUsername());
        List<PostCommentDto> commentDtos = comments.stream()
                .map(c -> new PostCommentDto(
                        c.getId(),
                        c.getContent(),
                        new PostAuthorDto(c.getAuthor().getId(), c.getAuthor().getUsername()),
                        c.getCreatedAt()
                ))
                .toList();
        return new PostDetailResponse(
                post.getId(),
                topicDto,
                post.getTitle(),
                post.getContent(),
                authorDto,
                post.getCreatedAt(),
                commentDtos
        );
    }
}
