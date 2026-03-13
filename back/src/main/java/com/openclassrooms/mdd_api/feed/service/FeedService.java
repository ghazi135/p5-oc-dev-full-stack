package com.openclassrooms.mdd_api.feed.service;

import com.openclassrooms.mdd_api.comment.repository.CommentRepository;
import com.openclassrooms.mdd_api.comment.repository.PostCommentCountRow;
import com.openclassrooms.mdd_api.feed.dto.FeedAuthorDto;
import com.openclassrooms.mdd_api.feed.dto.FeedItemDto;
import com.openclassrooms.mdd_api.feed.dto.FeedTopicDto;
import com.openclassrooms.mdd_api.post.entity.Post;
import com.openclassrooms.mdd_api.post.repository.PostRepository;
import com.openclassrooms.mdd_api.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<FeedItemDto> getFeed(Long userId, String order, Long topicId) {

        List<Long> topicIds = subscriptionRepository.findTopicIdsByUserId(userId);

        if (topicIds == null || topicIds.isEmpty()) {
            return List.of();
        }

        if (topicId != null) {
            if (!topicIds.contains(topicId)) {
                return List.of();
            }
            topicIds = List.of(topicId);
        }

        Sort sort = buildSort(order);
        List<Post> posts = postRepository.findByTopic_IdIn(topicIds, sort);

        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<PostCommentCountRow> rows = commentRepository.countByPostIds(postIds);
        Map<Long, Long> countsByPostId = rows.stream()
                .collect(Collectors.toMap(PostCommentCountRow::getPostId, row -> row.getCount()));

        return posts.stream()
                .map(p -> new FeedItemDto(
                        p.getId(),
                        new FeedTopicDto(p.getTopic().getId(), p.getTopic().getName()),
                        p.getTitle(),
                        p.getContent(),
                        new FeedAuthorDto(p.getAuthor().getId(), p.getAuthor().getUsername()),
                        p.getCreatedAt(),
                        countsByPostId.getOrDefault(p.getId(), 0L)
                ))
                .toList();
    }

    private static Sort buildSort(String order) {
        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(dir, "createdAt");
    }
}
