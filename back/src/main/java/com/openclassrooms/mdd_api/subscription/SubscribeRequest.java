package com.openclassrooms.mdd_api.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Requête d'abonnement à un thème (topicId). */
public record SubscribeRequest(
        @NotNull
        @Positive
        Long topicId
) {}
