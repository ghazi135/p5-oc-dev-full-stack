# Synthèse des tâches réalisées — Projet MDD (P5 Option B)

Ce document résume **les tâches que j’ai réalisées** sur le projet MDD et **comment elles répondent aux critères** du projet P5 « Prenez en charge le développement d'une application full‑stack complète » et de la grille d’auto‑évaluation P5 Option B ([document d’auto‑évaluation](https://s3.eu-west-1.amazonaws.com/course.oc-static.com/projects/4081-+Prenez+en+charge+le+d%C3%A9veloppement+d'une+application+full-stack+compl%C3%A8te/FAE+-+Option+B+-+P5+DFSJA+-editable.pdf)).

---

## 1. Architecture front-end & UI

- **Découpage par features** : `front/src/app/features/`
  - `auth/` (login, register, welcome, shell),
  - `feed/` (fil d’actualité),
  - `topics/` (liste & gestion des abonnements),
  - `profile/` (profil utilisateur),
  - `posts/` (création & détail d’article).
- **Architecture Angular moderne** :
  - Standalone components,
  - `app.routes.ts` avec lazy loading (`loadComponent`),
  - `auth-shell` comme layout authentifié (header + navigation) pour les routes protégées.
- **Gestion de l’auth côté front** :
  - `auth.store.ts` (signals) pour l’état d’authentification,
  - `auth.facade.ts` pour orchestrer login / refresh / logout,
  - `auth.interceptor.ts` pour gérer les 401 et le refresh automatique.
- **UX / UI** :
  - Angular Material (toolbar, boutons, formulaires),
  - Composants alignés avec les maquettes Figma (welcome, login, register, feed, topics, profile, post detail),
  - Navigation entre les principaux écrans du MVP.

➡ **Critères couverts** : architecture front claire et modulaire, liaisons front/back, conformité globale aux maquettes, base pour un rendu responsive et ergonomique.

---

## 2. Architecture back-end & API

- **Architecture par domaine + couches** dans `back/src/main/java/com/openclassrooms/mdd_api` :
  - Domaines : `auth`, `user`, `topic`, `subscription`, `feed`, `post`, `comment`.
  - Couches :
    - `controller/` : endpoints REST (`*Controller`),
    - `service/` : logique métier (`*Service`),
    - `dto/` : requêtes & réponses exposées au front (`*Request`, `*Response`, `*Dto`),
    - `entity/` : entités JPA,
    - `repository/` : interfaces `JpaRepository`,
    - `validation/` : règles spécifiques (ex. `PasswordPolicy`),
    - `bootstrap/` : seeders (topics, données de démonstration).
- **Contrat d’API formalisé** dans `docs/api-contract.md` :
  - Routes, méthodes HTTP, statuts,
  - Schémas de données (payloads, réponses),
  - Gestion des erreurs avec un **format unique** (`ApiErrorResponse`).
- **Documentation runtime** : Swagger/OpenAPI via `OpenApiConfig`, accessible sur `/swagger-ui.html` une fois le back démarré.

➡ **Critères couverts** : API REST conforme, structure claire des routes et contrôleurs, architecture back-end en couches selon les bonnes pratiques Spring Boot 3.

---

## 3. Authentification, sécurité, conformité

- **Sécurité Spring** (`SecurityConfig`) :
  - API stateless avec **JWT access token**,
  - **Refresh token** stocké en cookie **HttpOnly** (`refreshToken`),
  - **Access token** lu en priorité dans le header `Authorization: Bearer ...`, puis dans un cookie `accessToken` (cohérent avec le front).
- **CSRF** :
  - Cookie `XSRF-TOKEN` (non HttpOnly) + header `X-XSRF-TOKEN` sur toutes les requêtes mutantes,
  - `CsrfCookieFilter` + `SpaCsrfTokenRequestHandler` pour gérer correctement la logique SPA.
- **Gestion des erreurs de sécurité et d’accès** :
  - `RestExceptionHandler` traduit les exceptions Spring / métiers (401, 403, 404, 409, 400) en réponses JSON propres,
  - Aucun détail sensible dans les erreurs HTTP.
- **Conformité & confidentialité** :
  - `docs/privacy.md` décrit l’auteur, le contexte, les données traitées, les finalités, les cookies utilisés, les mesures de sécurité et les limites du MVP.

➡ **Critères couverts** : authentification/autorisation sécurisées, prévention des failles courantes via Spring Security, conformité (mentions légales, cookies techniques, logs non sensibles).

---

## 4. Fonctionnalités métier du MVP

- **Abonnements aux sujets** :
  - `TopicController` + `TopicService` pour lister les topics disponibles,
  - `SubscriptionController` + `SubscriptionService` pour s’abonner / se désabonner (`/api/users/me/subscriptions`).
- **Feed (fil d’actualité)** :
  - `FeedController` + `FeedService` : récupération des articles liés aux topics abonnés,
  - Tri asc/desc et filtrage par topic,
  - DTO `FeedItemDto` (titre, contenu, auteur, topic, date, nombre de commentaires).
- **Posts (articles)** :
  - Création : `POST /api/posts` (avec `CreatePostRequest`),
  - Détail : `GET /api/posts/{postId}` (`PostDetailResponse` avec topic, auteur, commentaires),
  - Règle métier : **abonnement obligatoire** au topic pour créer un article.
- **Commentaires** :
  - `CommentController` + `CommentService` pour publier un commentaire sur un article,
  - Règle métier : abonnement obligatoire au topic du post.
- **Profil** :
  - `UserMeController` + `UserService` pour :
    - consulter le profil (`GET /api/users/me`),
    - mettre à jour email, username, mot de passe (`PUT /api/users/me`),
  - Respect de la politique de mot de passe via `PasswordPolicy`.

➡ **Critères couverts** : implémentation de toutes les fonctionnalités décrites dans les spécifications (abonnement, création article, commentaires, feed, profil), cohérence front/back et validation métier côté back.

---

## 5. Tests unitaires, intégration et E2E

- **Back-end (JUnit 5)** :
  - Tests unitaires des services métiers :
    - `AuthServiceTest`, `JwtServiceTest`, `RefreshTokenServiceTest`,
    - `UserServiceTest`, `TopicServiceTest`, `FeedServiceTest`,
    - `SubscriptionServiceTest`, `PostServiceTest`, `CommentServiceTest`,
    - `PasswordPolicyTest`, `RestExceptionHandlerTest`, etc.
  - Tests d’intégration avec MockMvc et MySQL (Testcontainers) :
    - `AuthFlowIntegrationTest`,
    - `FeedControllerIntegrationTest`, `TopicControllerIntegrationTest`,
    - `SubscriptionControllerIntegrationTest`,
    - `PostControllerIntegrationTest`, `CommentControllerIntegrationTest`,
    - `UserMeIntegrationTest`.
- **Front-end (Jasmine/Karma)** :
  - Tests unitaires :
    - `auth.store.spec.ts`, `auth.facade.spec.ts`,
    - `auth.interceptor.spec.ts`, `auth.guards.spec.ts`,
    - services d’API (auth, feed, topics, profile, posts),
    - composants d’écran (login, register, welcome, feed, topics, profile, posts).
- **Tests end-to-end (Cypress)** :
  - `01-register-unique.cy.ts` : inscription avec email/username uniques,
  - `05-happy-path-full-flow.cy.ts` : parcours complet (inscription → login → abonnement → création post → commentaire → profil),
  - `06-auth-refresh-on-401-retry.cy.ts` : gestion du 401 + refresh token,
  - `10-topics-subscribe.cy.ts` : abonnement/désabonnement aux topics,
  - `20-post-create-and-comment.cy.ts` : création d’article et commentaire,
  - `30-profile-unsubscribe.cy.ts` : gestion des abonnements via profil,
  - `90-profile-update-password-and-logout.cy.ts` : mise à jour profil + logout.
- **Coverage** :
  - Back : JaCoCo (`mvn test` + rapport HTML/XML),
  - Front : `npm run test:coverage` → rapports HTML + `lcov.info`,
  - Scripts E2E avec coverage (Cypress + NYC) dans `front/README.md`.

➡ **Critères couverts** : tests unitaires sur les composants critiques, tests d’intégration et E2E, couverture mesurable, bonnes pratiques de test (AAA, nommage, isolation).

---

## 6. Qualité, performance, maintenance

- **Qualité du code** :
  - Architecture claire (packages / features, couches bien séparées),
  - DTOs dédiés pour éviter d’exposer les entités JPA,
  - Gestion centralisée des erreurs,
  - Refactoring du back pour passer à une structure controller/service/entity/repository/dto par domaine,
  - Simplification de la gestion des tokens côté front (plus de stockage du JWT en clair côté front).
- **Performance** :
  - API stateless, endpoints simples, JPA configuré avec les bonnes relations,
  - Possibilité de tester les perfs front via `npm run prod` (build prod + serveur statique),
  - Pas d’opérations coûteuses bloquantes dans les contrôleurs.
- **Conformité & maintenance** :
  - `docs/privacy.md` (confidentialité, données, cookies, logs),
  - README clairs (racine, back, front),
  - Structure de projet prête pour l’évolution (nouveaux domaines, endpoints, écrans).

➡ **Critères couverts** : code lisible et maintenable, corrections d’anomalies, application des principes SOLID et bonnes pratiques de refactoring, prise en compte des règles de conformité.

---

## 7. Documentation technique & utilisateur

- **Documentation technique** :
  - `README.md` (racine) : présentation du projet, stack, structure, quickstart,
  - `back/README.md` : prérequis Java, configuration `.env`, lancement, tests back,
  - `front/README.md` : commandes dev/prod, tests/unitaires, E2E, coverage.
- **Contrat d’API** :
  - `docs/api-contract.md` : endpoints, schémas de données, erreurs, cookies, CSRF, flux d’auth.
- **Conformité & sécurité** :
  - `docs/privacy.md` : mentions légales, données, finalités, cookies, sécurité.
- **FAQ utilisateur** :
  - `docs/faq-utilisateur.md` : connexion, inscription, déconnexion, publication, abonnements, profil, raisons d’un feed vide, etc.
- **Préparation soutenance & auto-évaluation** :
  - `docs/guide-presentation-soutenance.md` : lien entre les 8 étapes de la mission et le projet actuel,
  - `docs/verification-p5-auto-evaluation.md` : grille de critères P5, statut de chaque point + preuve dans le code.

➡ **Critères couverts** : documentation complète, structurée, claire pour un développeur et pour l’utilisateur, FAQ, aspects sécurité/confidentialité bien documentés.

---

## 8. Résumé pour la présentation

En résumé, à partir du dépôt de départ, j’ai :

- défini et mis en œuvre une **architecture complète** front + back adaptée au MVP MDD ;
- conçu et documenté une **API REST** conforme, sécurisée et alignée avec les specs ;
- mis en place une **authentification robuste** (JWT + refresh cookie HttpOnly + CSRF) et des règles métier cohérentes (abonnement requis pour publier/commenter) ;
- développé l’ensemble des **fonctionnalités** du MVP (abonnements, feed, posts, commentaires, profil) ;
- écrit une **batterie de tests** (unitaires, intégration, E2E) couvrant les flux critiques ;
- amélioré la **qualité, la conformité et la maintenabilité** du code ;
- rédigé une **documentation technique et utilisateur** complète (API, privacy, FAQ, guide de soutenance, auto-évaluation).

Ce README peut être utilisé comme support de justification lors de la soutenance pour montrer, point par point, comment le travail réalisé répond aux critères du projet P5 Option B.

