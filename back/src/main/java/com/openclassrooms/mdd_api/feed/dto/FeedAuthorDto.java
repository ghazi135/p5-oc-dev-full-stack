package com.openclassrooms.mdd_api.feed.dto;

/** DTO auteur (id, username) pour un item du feed. */
public record FeedAuthorDto(
        Long id,
        String username
) {}
