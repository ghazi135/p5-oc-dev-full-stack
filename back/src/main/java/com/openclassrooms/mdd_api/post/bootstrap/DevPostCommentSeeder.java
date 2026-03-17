package com.openclassrooms.mdd_api.post.bootstrap;

import com.openclassrooms.mdd_api.comment.entity.Comment;
import com.openclassrooms.mdd_api.comment.repository.CommentRepository;
import com.openclassrooms.mdd_api.post.entity.Post;
import com.openclassrooms.mdd_api.post.repository.PostRepository;
import com.openclassrooms.mdd_api.topic.entity.Topic;
import com.openclassrooms.mdd_api.topic.repository.TopicRepository;
import com.openclassrooms.mdd_api.user.entity.User;
import com.openclassrooms.mdd_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeder de démo : crée des posts et commentaires sur chaque thème si seed.demo.data=true et qu'aucun post n'existe.
 */
@Component
@Order(10)
@ConditionalOnProperty(prefix = "seed.demo", name = "data", havingValue = "true")
@RequiredArgsConstructor
public class DevPostCommentSeeder implements ApplicationRunner {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (postRepository.count() > 0) return;

        List<Topic> topics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (topics.isEmpty()) return;

        String rawPassword = "Aa1!aaaa"; // NOSONAR - demo seed only
        User bob = getOrCreateUser("bob_marley@example.com", "bob_marley", rawPassword);
        User tom = getOrCreateUser("tom_soyer@example.com", "tom_soyer", rawPassword);
        User ben = getOrCreateUser("ben_jerry@example.com", "ben_jerry", rawPassword);
        List<User> authors = List.of(bob, tom, ben);

        String seedPrefix = "[Seed] ";
        for (Topic topic : topics) {
            for (int i = 1; i <= 4; i++) {
                User author = authors.get((i - 1) % authors.size());
                Post post = postRepository.save(new Post(
                        seedPrefix + topic.getName() + " — Article " + i,
                        buildSeedContent(topic.getName(), i),
                        topic,
                        author
                ));
                if (i == 1) {
                    User commenter = (author.getId().equals(bob.getId())) ? tom : bob;
                    commentRepository.save(new Comment(
                            "Merci pour ce partage ! J'aime bien l'angle \"MVP\".",
                            post,
                            commenter
                    ));
                }
            }
        }
    }

    private String buildSeedContent(String topicName, int articleIndex) {
        return "Article de démonstration sur " + topicName + " (n°" + articleIndex + ").\n"
                + "On aborde une idée clé, avec un exemple simple et concret.\n"
                + "Ensuite on détaille un piège fréquent et comment l'éviter.\n"
                + "On ajoute une bonne pratique applicable dès aujourd'hui.\n"
                + "Enfin, on propose une petite checklist de vérification.\n"
                + "Conclusion : garde ça simple, puis itère.";
    }

    private User getOrCreateUser(String email, String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(
                        new User(email, username, passwordEncoder.encode(rawPassword))
                ));
    }
}
