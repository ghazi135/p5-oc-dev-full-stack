# Documentation — Projet MDD (P5)

Index de la documentation du livrable P5 « Prenez en charge le développement d'une application full-stack complète » (Option B).

---

## 1. Livrables principaux

| Fichier | Description |
|---------|-------------|
| [justification-choix-techniques.md](justification-choix-techniques.md) | Justification des choix techniques : objectifs, périmètre, architecture, stack, API, schémas de données, tests, revue technique, FAQ, annexes. |
| [api-contract.md](api-contract.md) | Contrat d’API : endpoints, schémas de données, cookies, CSRF, format d’erreur, flux d’authentification. |

---

## 2. Conformité

| Fichier | Description |
|---------|-------------|
| [faq-utilisateur.md](faq-utilisateur.md) | FAQ utilisateur : connexion, inscription, publication, abonnements, profil. |
| [privacy.md](privacy.md) | Conformité : mentions légales, données personnelles, cookies, sécurité (MVP). |

---

## 3. Architecture et schémas

| Fichier | Description |
|---------|-------------|
| [architecture_schema.svg](architecture_schema.svg) | Schéma global de l’architecture (Front Angular ↔ API Spring Boot ↔ MySQL). |
| [schema-relationnel-uml.png](schema-relationnel-uml.png) | Schéma relationnel UML des données (tables, colonnes, relations). |
| [schema-relationnel-mdd.sql](schema-relationnel-mdd.sql) | Script SQL de création des tables MySQL (`users`, `topics`, `posts`, `comments`, `subscriptions`, `refresh_tokens`). |

*Le schéma relationnel (UML + Mermaid) et le script SQL sont aussi détaillés dans [justification-choix-techniques.md](justification-choix-techniques.md) section 2.3.*

---

## 4. Annexes (captures d’écran)

| Ressource | Description |
|-----------|-------------|
| **[captures-ui.md](captures-ui.md)** | **Page d’affichage des captures d’écran de l’interface** (accueil, connexion, inscription, fil, thèmes, profil, articles). |
| [UI/](UI/) | Dossier des fichiers image des captures UI. |
| [coverage-back.png](coverage-back.png) | Capture de la couverture des tests back (JaCoCo). |
| [coverage-front.png](coverage-front.png) | Capture de la couverture des tests front (Istanbul/Karma). |
| [coverage-cypress.png](coverage-cypress.png) | Capture de la couverture des tests E2E (Cypress). |

---

## 5. Rapports générés (hors dépôt)

Après exécution des tests, les rapports complets sont disponibles en local :

- **Back (JaCoCo)** : `back/target/site/jacoco/index.html` (après `mvn test`).
- **Front (Karma)** : `front/coverage/index.html` (après `npm run test:coverage`).
- **Front (Cypress)** : `front/coverage/cypress/index.html` (après `npm run e2e:coverage`).
