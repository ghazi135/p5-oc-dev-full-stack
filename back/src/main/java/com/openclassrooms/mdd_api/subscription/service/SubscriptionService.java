package com.openclassrooms.mdd_api.subscription.service;

import com.openclassrooms.mdd_api.common.web.exception.ApiConflictException;
import com.openclassrooms.mdd_api.common.web.exception.ApiNotFoundException;
import com.openclassrooms.mdd_api.subscription.entity.Subscription;
import com.openclassrooms.mdd_api.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mdd_api.topic.entity.Topic;
import com.openclassrooms.mdd_api.topic.repository.TopicRepository;
import com.openclassrooms.mdd_api.user.entity.User;
import com.openclassrooms.mdd_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour les abonnements utilisateur aux thèmes.
 * <p>
 * Permet de s'abonner à un thème (une fois par utilisateur/thème) et de se désabonner.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    /**
     * Abonne l'utilisateur à un thème.
     *
     * @param userId  identifiant de l'utilisateur
     * @param topicId identifiant du thème
     * @return topicId (conformément au contrat API)
     * @throws ApiNotFoundException si le thème n'existe pas
     * @throws ApiConflictException  si l'utilisateur est déjà abonné au thème
     */
    @Transactional
    public Long subscribe(Long userId, Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ApiNotFoundException("Topic not found");
        }
        if (subscriptionRepository.existsByUser_IdAndTopic_Id(userId, topicId)) {
            throw new ApiConflictException("Already subscribed");
        }
        User userRef = userRepository.getReferenceById(userId);
        Topic topicRef = topicRepository.getReferenceById(topicId);
        try {
            subscriptionRepository.save(new Subscription(userRef, topicRef));
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Already subscribed");
        }
        return topicId;
    }

    /**
     * Désabonne l'utilisateur d'un thème. Idempotent (aucune erreur si non abonné).
     *
     * @param userId  identifiant de l'utilisateur
     * @param topicId identifiant du thème
     */
    @Transactional
    public void unsubscribe(Long userId, Long topicId) {
        subscriptionRepository.deleteByUser_IdAndTopic_Id(userId, topicId);
    }
}
