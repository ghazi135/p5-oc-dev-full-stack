# Guide de présentation — Soutenance P5 (Option B)

Ce document t’aide à préparer ta soutenance en reliant **chaque étape de la mission** (document « Prenez en charge le développement d'une application full-stack complète - OpenClassrooms.pdf ») à ce que le projet MDD contient.

---

## Contexte à rappeler en ouverture

- **Entreprise** : ORION  
- **Projet** : MDD (Monde de Dév) — réseau social pour développeurs (mise en relation, collaboration).  
- **Livrable** : un **MVP** (version minimale testée en interne) avec :
  - abonnement à des sujets (JavaScript, Java, Python, etc.),
  - fil d’actualité chronologique des articles,
  - rédaction d’articles et publication de commentaires.

**Rôle** : tu es responsable du MVP (décisions techniques, front, back, liaison, base de données, sécurité, documentation), avec l’équipe ORION (Heidi, Orlando, Juana).

**À mettre en avant pendant la soutenance** (comme demandé dans la mission) :
- Définition de l’architecture  
- Justification des choix techniques  
- Posture de supervision avec l’IA (tâches déléguées, revue et validation du code)  
- Revue technique  
- Documentation pour les collègues et les utilisateurs  

---

## Étape 1 — Examiner les spécifications et le code existant

**Ce que la mission demande :**  
Prendre connaissance du contexte, du repo, de la vidéo et des maquettes ; comprendre les attentes fonctionnelles et techniques ; analyser le code fourni.

**Ce que tu peux montrer / dire :**

- Tu as pris connaissance des **spécifications fonctionnelles** et des **contraintes techniques** (Orlando), du **dépôt initial** (Heidi) et des **maquettes** (Juana).
- Tu as identifié les **modules déjà amorcés** et ce qu’il restait à compléter ou corriger (ex. structure des packages, auth, endpoints).
- **Livrables liés :** README (structure du repo, stack), `docs/api-contract.md` (aligné aux specs).

**Phrase d’accroche possible :**  
« J’ai commencé par une analyse du repo et des specs pour bien cadrer le périmètre du MVP et ne pas ajouter de fonctionnalités hors scope. »

---

## Étape 2 — Définir l’architecture logicielle et l’API

**Ce que la mission demande :**  
Concevoir l’architecture complète (front + back), choisir librairies, frameworks, design patterns et outils, définir la structure de l’API (endpoints, schémas, communication front/back). Compléter le template de documentation des choix techniques.

**Ce que tu peux montrer / dire :**

- **Front :** Angular (standalone), structure par **features** (auth, feed, topics, profile, posts), **signals** pour l’état auth, lazy loading des routes, **Angular Material** pour l’UI.
- **Back :** Spring Boot 3, Java 21, architecture par **domaine** avec couches (controller, service, dto, entity, repository, validation, bootstrap).
- **API :** REST sous `/api`, format JSON, **contrat documenté** dans `docs/api-contract.md`, **Swagger/OpenAPI** pour les endpoints.
- **Communication :** proxy Angular en dev (`/api` → back), gestion des erreurs unifiée (`ApiErrorResponse`), auth (JWT + refresh token + CSRF).
- **Livrables :** `docs/justification-choix-techniques.pdf` (ou équivalent), `docs/api-contract.md` (endpoints, schémas, flux).

**Phrase d’accroche possible :**  
« J’ai défini une architecture client-serveur en couches, avec une API REST claire et documentée, pour que le front et le back évoluent sans se gêner. »

---

## Étape 3 — Préparer l’environnement de développement

**Ce que la mission demande :**  
Repository GitHub opérationnel, IDE configuré, base de données installée et cohérente avec le modèle, dépendances à jour, éventuellement Gitflow et données de test.

**Ce que tu peux montrer / dire :**

- **Repo :** mono-repo (front + back + docs), propre et à jour.
- **Base de données :** **MySQL** via **Docker** (`back/compose.yml`), schéma géré par l’**ORM JPA** (entités User, Topic, Subscription, Post, Comment, RefreshToken).
- **Config :** `back/.env` pour la DB et les secrets (JWT, etc.), `front/proxy.conf.json` pour le proxy vers l’API.
- **Données de test :** seeders (`TopicSeeder`, `DevPostCommentSeeder` optionnel) pour peupler la base.
- **Versions :** Angular (20), Java 21, Spring Boot 3, comme recommandé (mission évoque Angular 19, Java 21, Spring Boot 3).

**Phrase d’accroche possible :**  
« L’environnement est reproductible : clone, Docker pour MySQL, variables dans .env, puis lancement front et back selon le README. »

---

## Étape 4 — Implémenter une action simple de bout en bout

**Ce que la mission demande :**  
Une fonctionnalité simple qui traverse front → back → base de données pour valider les choix techniques et la stabilité. Déléguer des tâches à l’IA, puis relire et valider le code.

**Ce que tu peux montrer / dire :**

- **Exemple de chaîne complète :** par exemple **« s’inscrire »** ou **« s’abonner à un thème »** : formulaire front → appel API (avec CSRF/cookies) → contrôleur → service → repository → base (User ou Subscription).
- Tu as **identifié des tâches simples** (ex. DTOs, validations, mappers, tests unitaires) déléguées à l’IA, puis **revues et validées** (posture de supervision).
- Le **document des choix techniques** a été mis à jour au fur et à mesure.

**Phrase d’accroche possible :**  
« J’ai validé l’architecture avec un premier flux complet, par exemple l’inscription ou l’abonnement, et j’ai utilisé l’IA pour des parties bien délimitées en gardant la main sur la revue. »

---

## Étape 5 — Implémenter les fonctionnalités principales

**Ce que la mission demande :**  
Toutes les fonctionnalités des spécifications : abonnement aux sujets, création d’articles, commentaires, fil d’actualité — sans sur-fonctionnalité. Cohérence des données front/back, gestion des erreurs (messages utilisateur et logs serveur). Tests pour chaque fonctionnalité.

**Ce que tu peux montrer / dire :**

- **Abonnements :** `GET /api/topics`, `POST/DELETE /api/users/me/subscriptions` (liste, abonnement, désabonnement).
- **Articles :** `POST /api/posts`, `GET /api/posts/:id` (création, détail). Règle métier : abonnement au thème obligatoire.
- **Commentaires :** `POST /api/posts/:postId/comments`. Même règle : abonnement au thème du post.
- **Fil d’actualité :** `GET /api/feed` (tri asc/desc, filtre par thème), basé sur les abonnements.
- **Profil :** `GET/PUT /api/users/me` (consultation, mise à jour email / username / mot de passe).
- **Cohérence :** DTOs partagés, format d’erreur unifié, validation côté back (Bean Validation, politique mot de passe).
- **Tests :** un test (unitaire ou intégration) par fonctionnalité critique (auth, feed, topics, subscriptions, posts, comments, user).

**Phrase d’accroche possible :**  
« J’ai strictement respecté les specs fonctionnelles : abonnements, feed, articles, commentaires, profil, sans ajouter de scope. Chaque bloc métier a ses tests. »

---

## Étape 6 — Appliquer la mise en forme graphique et la sécurité

**Ce que la mission demande :**  
Intégrer les maquettes Figma, application responsive, hiérarchie visuelle, espacements, lisibilité, accessibilité de base. Sécuriser les échanges avec le back (Spring Security, JWT ou Basic Auth). Ne pas sur-complexifier la sécurité pour un MVP interne.

**Ce que tu peux montrer / dire :**

- **UI :** Angular Material (toolbar, sidenav, formulaires, boutons), structure par écrans (welcome, login, register, feed, topics, profile, détail post). Respect **global** des écrans (pas obligatoirement pixel perfect).
- **Responsive :** layout adapté desktop / mobile (sidenav, menu).
- **Sécurité :**
  - **Authentification :** JWT (access token) + **refresh token en cookie HttpOnly** (session persistante), endpoint `/api/auth/refresh`.
  - **CSRF** : cookie `XSRF-TOKEN` + header `X-XSRF-TOKEN` sur les requêtes mutantes (POST, PUT, DELETE).
  - **Spring Security :** `SecurityConfig`, `BearerTokenResolver` (header + cookie), endpoints publics (register, login, csrf) vs protégés (`authenticated()`).
  - **Données :** mots de passe hashés (BCrypt), pas de fuite d’infos sensibles dans les réponses ni dans les logs (`RestExceptionHandler`).

**Phrase d’accroche possible :**  
« J’ai appliqué les maquettes avec Angular Material et mis en place une sécurité adaptée au MVP : JWT, refresh token en cookie HttpOnly et CSRF, sans sur-complexité. »

---

## Étape 7 — Tester, revue technique et documentation

**Ce que la mission demande :**  
Rapport de tests (résultats, couverture, outils), rapport de revue technique (forces, axes d’amélioration), documentation technique claire et FAQ utilisateur. Outils recommandés : JUnit, Jest, Cypress. Couverture ≥ 70 % si possible. Documenter les endpoints, FAQ (cas d’usage, erreurs courantes, solutions).

**Ce que tu peux montrer / dire :**

- **Tests unitaires :** Back (JUnit 5) : AuthService, JwtService, RefreshTokenService, PasswordPolicy, UserService, TopicService, FeedService, CommentService, PostService, RestExceptionHandler, etc. Front (Jasmine/Karma) : auth (store, facade, guards, intercepteur), composants, services.
- **Tests d’intégration :** Back : contrôleurs (Auth, Feed, Topics, Subscriptions, Posts, Comments, User/me) avec MockMvc et base (Testcontainers MySQL).
- **E2E :** Cypress (inscription, connexion, refresh 401, parcours complet, thèmes, posts/commentaires, profil, déconnexion).
- **Couverture :** JaCoCo (back), Istanbul/NYC (front), rapports HTML + LCOV.
- **Documentation :** README (structure, quickstart, config), `docs/api-contract.md` (endpoints, schémas), `docs/faq-utilisateur.md` (connexion, publication, abonnement, profil), `docs/privacy.md` (conformité, cookies, données).
- **Revue technique :** à résumer en quelques points (forces du projet, 2–3 axes d’amélioration, recommandations).

**Phrase d’accroche possible :**  
« Les tests couvrent les parties critiques en unitaire et en intégration, et Cypress valide les parcours utilisateur. La doc technique et la FAQ permettent à un collègue de reprendre le projet et à un utilisateur de comprendre les actions principales. »

---

## Étape 8 — Finaliser le code

**Ce que la mission demande :**  
Nettoyer le code (doublons, indentation, commentaires Javadoc), bonnes pratiques, principes SOLID, documentation du code. État du repo propre, branches mergées, branche de release / tag. Penser au lecteur du code et de la doc.

**Ce que tu peux montrer / dire :**

- **Structure back :** packages par domaine et par couche (controller, service, dto, entity, repository, validation, bootstrap), séparation des responsabilités (SOLID).
- **Conventions :** nommage cohérent, `@Operation` / `@ApiResponse` sur les contrôleurs, Javadoc sur les classes sensibles (ex. SecurityConfig, RestExceptionHandler).
- **Repo :** code à jour, README et docs dans `docs/`, pas de code mort évident.
- **Git :** commits réguliers, branche principale stable, éventuellement tag de release pour la livraison à Orlando.

**Phrase d’accroche possible :**  
« J’ai finalisé en structurant le back en couches par domaine, en documentant les points sensibles et en m’assurant qu’un autre développeur peut comprendre et faire tourner le projet avec le README et la doc. »

---

## Livrables pour Orlando (à citer en soutenance)

La mission demande de transmettre à Orlando **la documentation (et annexes)** + **le dépôt GitHub** avec une version stable contenant :

| Livrable | Où c’est dans le projet |
|----------|--------------------------|
| Architecture et code front-end | `front/`, structure features + shell + core + shared |
| Architecture back-end + API | `back/` (packages par domaine), `docs/api-contract.md`, Swagger |
| Code back et données sécurisées | Auth JWT + refresh + CSRF, validation, RestExceptionHandler, .env |
| Code et instructions des tests | `back/` (JUnit, Testcontainers), `front/` (Jasmine, Cypress), README (commandes) |
| Code amélioré, conventions | Structure en couches, nommage, Javadoc, SOLID |
| README technique + configuration | README racine, `front/README.md`, `back/README.md`, `docs/` |

Tu peux dire : « Tous les livrables demandés pour Orlando sont dans le repo et dans la doc : architecture, code, tests, sécurité et documentation technique + FAQ. »

---

## Plan de parole suggéré (environ 5–7 min par bloc)

1. **Contexte et objectifs** (1–2 min) : ORION, MDD, MVP, ton rôle et les attentes (architecture, justification, IA, revue, doc).
2. **Architecture et choix techniques** (2–3 min) : Étape 2 + 3. Montrer un schéma ou l’arborescence front/back, citer le contrat API et la justification.
3. **Fonctionnalités et sécurité** (2–3 min) : Étapes 4, 5, 6. Un flux de démo (ex. inscription → login → feed → abonnement → post) + enchaîner sur la sécurité (JWT, cookie, CSRF).
4. **Tests et documentation** (2 min) : Étape 7. Outils, couverture, FAQ, api-contract, privacy.
5. **Finalisation et livrables** (1 min) : Étape 8 + rappel des livrables pour Orlando.
6. **Posture IA et revue technique** (1–2 min) : exemples de tâches déléguées à l’IA et comment tu les as validées ; 2–3 points de revue (forces, axes d’amélioration).

Tu peux t’appuyer sur `docs/verification-p5-auto-evaluation.md` pour les preuves par critère et sur `docs/faq-utilisateur.md` pour montrer la FAQ utilisateur.
