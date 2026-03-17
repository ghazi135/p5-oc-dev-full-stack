package com.openclassrooms.mdd_api.comment.service;

import com.openclassrooms.mdd_api.comment.dto.CreateCommentRequest;
import com.openclassrooms.mdd_api.comment.entity.Comment;
import com.openclassrooms.mdd_api.comment.repository.CommentRepository;
import com.openclassrooms.mdd_api.common.web.exception.ApiNotFoundException;
import com.openclassrooms.mdd_api.post.entity.Post;
import com.openclassrooms.mdd_api.post.repository.PostRepository;
import com.openclassrooms.mdd_api.subscription.repository.SubscriptionRepository;
import com.openclassrooms.mdd_api.user.entity.User;
import com.openclassrooms.mdd_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour la gestion des commentaires sur les articles.
 * <p>
 * Règle métier : l'utilisateur doit être abonné au thème du post pour pouvoir ajouter un commentaire.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String FORBIDDEN = "Forbidden";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Ajoute un commentaire à un article.
     *
     * @param userId identifiant de l'utilisateur auteur
     * @param postId identifiant du post
     * @param req    contenu du commentaire
     * @return l'identifiant du commentaire créé
     * @throws ApiNotFoundException  si le post ou l'utilisateur n'existe pas
     * @throws AccessDeniedException si l'utilisateur n'est pas abonné au thème du post
     */
    @Transactional
    public Long addComment(Long userId, Long postId, CreateCommentRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiNotFoundException("Post not found"));
        Long topicId = post.getTopic().getId();
        boolean subscribed = subscriptionRepository.existsByUser_IdAndTopic_Id(userId, topicId);
        if (!subscribed) throw new AccessDeniedException(FORBIDDEN);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ApiNotFoundException("User not found"));
        Comment comment = new Comment(req.content(), post, author);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }
}
