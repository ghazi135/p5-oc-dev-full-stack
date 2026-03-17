package com.openclassrooms.mdd_api.common.security;

import com.openclassrooms.mdd_api.common.web.exception.ApiUnauthorizedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Centralise l'extraction de l'identifiant de l'utilisateur actuel à partir du sujet JWT.
 */
@Component
public class CurrentUserIdExtractor {

    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";

    /**
     * Extrait l'identifiant utilisateur (Long) depuis le subject du JWT.
     *
     * @param jwt JWT authentifié (peut être null)
     * @return l'id utilisateur
     * @throws ApiUnauthorizedException si JWT null, subject null ou non numérique
     */
    public Long requireUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new ApiUnauthorizedException(UNAUTHORIZED_MESSAGE);
        }
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException ex) {
            throw new ApiUnauthorizedException(UNAUTHORIZED_MESSAGE);
        }
    }
}
