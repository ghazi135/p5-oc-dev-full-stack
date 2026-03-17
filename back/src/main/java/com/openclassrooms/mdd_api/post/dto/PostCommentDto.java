package com.openclassrooms.mdd_api.post.dto;

import java.time.Instant;

/** DTO commentaire (id, contenu, auteur, date) dans le détail d'un post. */
public record PostCommentDto(
        Long id,
        String content,
        PostAuthorDto author,
        Instant createdAt
) {}
