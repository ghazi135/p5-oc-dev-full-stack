package com.openclassrooms.mdd_api.topic.dto;

/** DTO thème pour la liste : id, name, description, subscribed. */
public record TopicListItemDto(
        Long id,
        String name,
        String description,
        boolean subscribed
) {}
