-- =============================================================================
-- Schéma relationnel MDD — Création des tables (MySQL 8)
-- Aligné sur les entités JPA du back (User, Topic, Post, Comment, Subscription, RefreshToken).
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- users : comptes utilisateurs
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS topics;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    email           VARCHAR(254) NOT NULL,
    username        VARCHAR(50)  NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- topics : thèmes (sujets) auxquels les utilisateurs peuvent s'abonner
-- -----------------------------------------------------------------------------
CREATE TABLE topics (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topics_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- posts : articles publiés sur un thème par un utilisateur
-- -----------------------------------------------------------------------------
CREATE TABLE posts (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    topic_id    BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    KEY fk_posts_topic (topic_id),
    KEY fk_posts_author (user_id),
    CONSTRAINT fk_posts_topic  FOREIGN KEY (topic_id) REFERENCES topics (id),
    CONSTRAINT fk_posts_author FOREIGN KEY (user_id)  REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- comments : commentaires sur un article, par un utilisateur
-- -----------------------------------------------------------------------------
CREATE TABLE comments (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    content     VARCHAR(2000) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    KEY fk_comments_post (post_id),
    KEY fk_comments_author (user_id),
    CONSTRAINT fk_comments_post   FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- subscriptions : abonnement d'un utilisateur à un thème (N-N user ↔ topic)
-- -----------------------------------------------------------------------------
CREATE TABLE subscriptions (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    user_id  BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_subscriptions_user_topic (user_id, topic_id),
    KEY idx_subscriptions_user_id (user_id),
    KEY idx_subscriptions_topic_id (topic_id),
    CONSTRAINT fk_subscriptions_user  FOREIGN KEY (user_id)  REFERENCES users (id),
    CONSTRAINT fk_subscriptions_topic FOREIGN KEY (topic_id) REFERENCES topics (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- refresh_tokens : tokens de rafraîchissement (hash) pour la session
-- -----------------------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id          BIGINT     NOT NULL AUTO_INCREMENT,
    user_id     BIGINT     NOT NULL,
    token_hash  VARCHAR(64) NOT NULL,
    expires_at  TIMESTAMP  NOT NULL,
    revoked_at  TIMESTAMP  NULL DEFAULT NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_hash (token_hash),
    KEY ix_refresh_tokens_user (user_id),
    KEY ix_refresh_tokens_expires (expires_at),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
