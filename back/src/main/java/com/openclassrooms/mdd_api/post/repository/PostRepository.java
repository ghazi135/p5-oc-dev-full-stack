package com.openclassrooms.mdd_api.post.repository;

import com.openclassrooms.mdd_api.post.entity.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Repository JPA des articles (posts) : recherche par thèmes, détail avec topic et auteur. */
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"topic", "author"})
    List<Post> findByTopic_IdIn(Collection<Long> topicIds, Sort sort);

    @EntityGraph(attributePaths = {"topic", "author"})
    Optional<Post> findDetailById(Long id);
}
