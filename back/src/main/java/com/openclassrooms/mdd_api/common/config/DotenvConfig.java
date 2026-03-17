package com.openclassrooms.mdd_api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

/**
 * Charge le fichier .env à la racine du projet dans l'environnement Spring (optional).
 */
@SuppressWarnings("java:S1118")
@Configuration
public class DotenvConfig {

    /**
     * Configure le chargement optionnel du fichier .env pour les variables d'environnement.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(".env"));
        configurer.setIgnoreResourceNotFound(true);
        return configurer;
    }
}
