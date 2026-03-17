package com.openclassrooms.mdd_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Point d'entrée de l'API MDD (Monde de Dév) : application Spring Boot exposant l'API REST
 * d'authentification, thèmes, abonnements, fil d'actualité, articles et commentaires.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MddApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MddApiApplication.class, args);
    }
}
