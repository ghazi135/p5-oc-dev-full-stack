# Back — MDD API (Spring Boot)

API REST Spring Boot (Java 21) + MySQL (Docker).

---

## Prérequis : JDK 17 ou 21

Si vous voyez **« release version 21 not supported »** ou **« Fatal error compiling »**, Maven utilise un JDK trop ancien (ex. Java 11).

1. **Installer JDK 21** (recommandé) : [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21&os=windows) ou [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21).
2. **Définir `JAVA_HOME`** (PowerShell, session courante) :
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.x-hotspot"   # adapter le chemin
   ```
3. Relancer depuis `back/` : `.\mvnw.cmd spring-boot:run` ou `.\run.ps1`.

---

## Configuration (`back/.env`)

Créer un fichier `back/.env` (non versionné) au niveau de `back/` (format `key=value`).

Exemple minimal (dev) :

```properties
DB_NAME=mdd
DB_USER=mdd
DB_PASSWORD=mdd
DB_ROOT_PASSWORD=root_password

DB_HOST=localhost
DB_PORT=3307

JWT_EXPIRATION_MS=900000
REFRESH_TOKEN_EXPIRATION_MS=1209600000
TOKEN_SECRET=dev-secret-change-me-in-production-min-32-bytes

SEED_DEMO_DATA=false
```

## Démarrer MySQL (Docker)

⚠️ À exécuter depuis back/ (Docker Compose lit le .env du dossier courant) :

```bash
cd back
docker compose up -d
```

MySQL : localhost:3307 → container 3306.

## Lancer l’API
**`.\run.ps1`** (vérifie JDK 17+ puis lance l’API) — ou : `./mvnw spring-boot:run` (avec `-Dmaven.test.skip=true` pour ignorer les tests).

API : http://localhost:8080

## Tests & coverage (JaCoCo)
```bash
./mvnw test
```

Rapports :

HTML : back/target/site/jacoco/index.html
XML : back/target/site/jacoco/jacoco.xml

## Documentation Swagger (OpenAPI)

Une fois l’application démarrée :

* Swagger UI :
  👉 http://localhost:8080/swagger-ui.html

* Spécification OpenAPI (JSON) :
  👉 http://localhost:8080/v3/api-docs

Toutes les routes sont documentées via @Operation et @ApiResponse dans les contrôleurs.