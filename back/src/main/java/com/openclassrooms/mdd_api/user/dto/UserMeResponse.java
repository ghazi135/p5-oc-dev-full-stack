package com.openclassrooms.mdd_api.user.dto;

import com.openclassrooms.mdd_api.topic.dto.TopicDto;

import java.util.List;

/** Réponse profil utilisateur connecté : id, email, username, liste des thèmes abonnés. */
public record UserMeResponse(
        Long id,
        String email,
        String username,
        List<TopicDto> subscriptions
) {}
