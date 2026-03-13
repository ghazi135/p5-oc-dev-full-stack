# Vérification P5 — Auto-évaluation (Option B - Full-Stack Java Angular)

Ce document permet de cocher les indicateurs de réussite du [document d'auto-évaluation P5](https://s3.eu-west-1.amazonaws.com/course.oc-static.com/projects/4081-+Prenez+en+charge+le+d%C3%A9veloppement+d'une+application+full-stack+compl%C3%A8te/FAE+-+Option+B+-+P5+DFSJA+-editable.pdf) en s'appuyant sur l'état actuel du projet MDD.

---

## 1. Définir l'architecture Front-end et développer les composants et interfaces

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Architecture front-end claire, modulaire et structurée | ✅ | `front/src/app` : `core/`, `features/` (auth, feed, topics, profile, posts), `shell/`, `shared/`. Routes lazy-loaded, guards, facade/store pour l’auth. |
| ☐ Conventions Angular 19 (nomenclature, arborescence, bonnes pratiques) | ✅ | Standalone components, signals (auth.store), `loadComponent` pour le lazy loading, structure par feature. Projet en Angular 20 (conventions alignées). |
| ☐ Composants correspondant aux maquettes Figma et spécifications MDD | À vérifier | À confronter avec les maquettes Juana : feed, topics, profile, posts, login/register. |
| ☐ Liaisons front/back (services, endpoints, observables/signals) | ✅ | `*ApiService` par feature (feed, topics, profile, posts), `auth-api.service`, intercepteur 401/refresh, `withCredentials` pour les cookies. |
| ☐ Interface responsive, ergonomique, fonctionnelle desktop et mobile | À vérifier | Angular Material (toolbar, sidenav, etc.). Tester manuellement sur desktop et mobile. |
| ☐ Documentation des composants clés et de l’architecture UI, avec captures d’écran | ⚠️ | README (racine, front, back) décrivent structure et quickstart. **À compléter** : captures d’écran des écrans principaux et schéma/texte sur l’architecture UI dans la doc. |

---

## 2. Analyser et concevoir une API pour intégrer front et back

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ API REST conforme, structure claire des routes, ressources et contrôleurs | ✅ | `docs/api-contract.md` + contrôleurs sous `back/…/controller/` : auth, feed, topics, subscriptions, posts, comments, users/me. Base path `/api`. |
| ☐ Endpoints, schémas de données et formats d’échange documentés | ✅ | `docs/api-contract.md` (contrat détaillé). Swagger/OpenAPI : `http://localhost:8080/swagger-ui.html`, `@Operation` / `@ApiResponse` dans les contrôleurs. |
| ☐ Communication front/back fluide et sécurisée (auth + gestion des erreurs) | ✅ | JWT + refresh cookie HttpOnly, CSRF (XSRF-TOKEN + header), intercepteur 401 + refresh puis retry. Format d’erreur unifié (`ApiErrorResponse`, `RestExceptionHandler`). |

---

## 3. Prendre en charge l’implémentation de l’architecture back-end

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Back-end structuré selon les bonnes pratiques Spring Boot 3 | ✅ | Packages par domaine (auth, comment, feed, post, subscription, topic, user) avec `controller/`, `service/`, `dto/`, `entity/`, `repository/`, `validation/`, `bootstrap/`. |
| ☐ Authentification et autorisation sécurisées | ✅ | JWT (access token) + refresh token (cookie HttpOnly), CSRF, `SecurityConfig`, `BearerTokenResolver` (header + cookie accessToken). |
| ☐ Protection des accès aux données | ✅ | Endpoints protégés `authenticated()`, règles métier (abonnement requis pour créer un post/commentaire). |
| ☐ Architecture évolutive et scalable | ✅ | Couches claires, services injectables, JPA/MySQL. Possibilité d’ajouter cache, async, etc. |
| ☐ Conventions Java : nommage, formatage, annotations, Javadoc | ✅ | Nommage cohérent, `@Tag` / `@Operation` / `@ApiResponse`, Javadoc sur classes sensibles. |
| ☐ Logs et exceptions ne divulguent pas d’informations sensibles | ✅ | `RestExceptionHandler` renvoie des messages génériques côté API ; pas de stack trace ni mot de passe dans les réponses. |

---

## 4. Sécurité (usage des frameworks)

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Prévention des failles courantes via les frameworks sécurisés | ✅ | Spring Security (JWT, CSRF, session stateless), validation (Bean Validation), mot de passe hashé (BCrypt), cookie HttpOnly/SameSite. |

---

## 5. Mettre en œuvre les tests

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Tests unitaires sur les composants critiques (front et back) | ✅ | **Back** : JUnit 5 (AuthService, JwtService, RefreshTokenService, PasswordPolicy, UserService, TopicService, FeedService, CommentService, PostService, RestExceptionHandler, etc.). **Front** : Jasmine/Karma (auth.store, auth.facade, guards, intercepteur, composants auth, feed, profile, topics, posts). |
| ☐ Tests d’intégration et E2E pour le parcours utilisateur | ✅ | **Back** : tests d’intégration (Auth, Feed, Topics, Subscriptions, Posts, Comments, User/me) avec MockMvc + MySQL (Testcontainers). **Front** : Cypress E2E (register, login, refresh 401, happy path, topics, posts/comments, profile, logout). |
| ☐ Couverture suffisante et rapports lisibles | ✅ | JaCoCo (back), Istanbul/NYC (front), rapports HTML + LCOV. |
| ☐ Bonnes pratiques (nommage, isolation, Arrange-Act-Assert) | ✅ | Tests avec `@DisplayName`, mocks isolés, structure AAA repérable. |
| ☐ Anomalies corrigées et actions correctives documentées | À documenter | Indiquer dans la doc ou le README les bugs corrigés et les correctifs (ex. token, CSRF, 401). |
| ☐ Outils d’analyse et d’optimisation utilisés | ✅ | Outils de tests et de couverture (JaCoCo, Istanbul/NYC) et analyse de code (linters, revues). |

---

## 6. Assurer la performance, la conformité et la maintenance

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Code clair, lisible et documenté pour la maintenance | ✅ | README, api-contract, privacy, structure de packages explicite, commentaires sur les choix (CSRF, abonnement requis). |
| ☐ Anomalies relevées en revue ou en tests corrigées | À documenter | À compléter selon l’historique de revue. |
| ☐ Outils de qualité et d’analyse intégrés et compris | ✅ | Scripts de test et de coverage, et usage d’outils d’analyse statique/linting. |
| ☐ Cohérence, modularité et robustesse (ajustements ciblés) | ✅ | Refactoring récent (packages back par couche, simplification token front). |
| ☐ Principes SOLID et bonnes pratiques de refactoring | ✅ | Séparation controller / service / repository, DTOs, validation dédiée. |
| ☐ Performance front et back optimisée | À vérifier | Pas de surcharge évidente ; possibilité de mesurer avec Lighthouse (README front : `npm run prod` sur 4300). |
| ☐ Conformité (mentions légales, politique de confidentialité, sécurité des logs) | ✅ | `docs/privacy.md` : mentions légales MVP, données traitées, cookies, sécurité, pas de données sensibles dans les logs. |

---

## 7. Rédiger la documentation technique

| Indicateur | Statut | Preuve / Notes |
|------------|--------|----------------|
| ☐ Documentation complète (template fourni), structure, technologies, installation | ✅ | README racine + front/back : structure, stack, prérequis, quickstart (clone, DB, back, front). |
| ☐ Endpoints API, schémas de données et dépendances techniques détaillés | ✅ | `docs/api-contract.md` + Swagger/OpenAPI. |
| ☐ Configuration d’environnement et déploiement | ✅ | README (ports, .env back, Docker MySQL), `back/README.md` (JDK, variables). |
| ☐ Documentation claire, synthétique et accessible | ✅ | Markdown structuré, sections identifiées. |
| ☐ FAQ utilisateur (connexion, publication, abonnement, profil) | ✅ | `docs/faq-utilisateur.md` : connexion, inscription, déconnexion, publication, commentaires, abonnements, profil. |
| ☐ Documentation respecte sécurité et confidentialité | ✅ | `docs/privacy.md` traite cookies, données personnelles, mesures de sécurité. |
| ☐ Documentation visuellement claire (mise en page, contrastes, lisibilité) | À vérifier | Markdown standard lisible ; export PDF possible pour soutenance. |

---

## Synthèse des actions recommandées avant dépôt

1. **Captures d’écran** : ajouter au moins une capture par écran principal (welcome, login, feed, topics, profile, détail post) dans la documentation (ou README) pour illustrer l’architecture UI.
2. **Justification des choix techniques** : s’assurer que le fichier `docs/justification-choix-techniques.pdf` existe et est à jour (cité dans le README).
3. **Vérifications manuelles** : confronter l’UI aux maquettes Figma ; tester le responsive ; documenter brièvement les anomalies corrigées et les optimisations si besoin.

Une fois ces points traités, vous pouvez cocher toutes les cases du document d’auto-évaluation et déposer vos livrables sur la plateforme.
