package com.openclassrooms.mdd_api.post.dto;

import java.time.Instant;

public record PostCommentDto(
        Long id,
        String content,
        PostAuthorDto author,
        Instant createdAt
) {}
