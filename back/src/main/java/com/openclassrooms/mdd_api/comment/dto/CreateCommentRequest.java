package com.openclassrooms.mdd_api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Requête de création d'un commentaire (contenu). */
public record CreateCommentRequest(
        @NotBlank @Size(max = 2_000) String content
) {}
