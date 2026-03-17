package com.openclassrooms.mdd_api.topic.service;

import com.openclassrooms.mdd_api.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mdd_api.topic.dto.TopicListItemDto;
import com.openclassrooms.mdd_api.topic.entity.Topic;
import com.openclassrooms.mdd_api.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service métier pour la liste des thèmes (topics).
 * <p>
 * Retourne tous les thèmes avec un indicateur d'abonnement pour l'utilisateur connecté.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Liste tous les thèmes triés par nom, avec l'indicateur subscribed pour l'utilisateur.
     *
     * @param userId identifiant de l'utilisateur connecté
     * @return liste des thèmes avec subscribed = true/false
     */
    @Transactional(readOnly = true)
    public List<TopicListItemDto> listTopics(Long userId) {
        List<Topic> topics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        Set<Long> subscribedIds = new HashSet<>(subscriptionRepository.findTopicIdsByUserId(userId));
        return topics.stream()
                .map(t -> new TopicListItemDto(t.getId(), t.getName(), t.getDescription(), subscribedIds.contains(t.getId())))
                .toList();
    }
}
