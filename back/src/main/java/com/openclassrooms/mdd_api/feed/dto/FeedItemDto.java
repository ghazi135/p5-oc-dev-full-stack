package com.openclassrooms.mdd_api.feed.dto;

import java.time.Instant;

/** Item du fil d'actualité : post avec topic, auteur, date et nombre de commentaires. */
public record FeedItemDto(
        Long id,
        FeedTopicDto topic,
        String title,
        String content,
        FeedAuthorDto author,
        Instant createdAt,
        long commentsCount
) {}
