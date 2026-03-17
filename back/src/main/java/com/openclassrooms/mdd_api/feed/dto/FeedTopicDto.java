package com.openclassrooms.mdd_api.feed.dto;

/** DTO thème minimal (id, name) pour un item du feed. */
public record FeedTopicDto(
        Long id,
        String name
) {}
