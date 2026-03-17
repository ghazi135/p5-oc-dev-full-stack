# Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn -B dependency:go-offline -DskipTests

COPY src ./src
RUN mvn -B package -DskipTests -q && \
    cp target/*.jar app.jar

# Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache dumb-init wget
COPY --from=build /app/app.jar .

EXPOSE 8080
ENTRYPOINT ["dumb-init", "--"]
# Délai pour laisser MySQL accepter les connexions après le healthcheck
CMD ["sh", "-c", "sleep 15 && exec java -jar app.jar"]
