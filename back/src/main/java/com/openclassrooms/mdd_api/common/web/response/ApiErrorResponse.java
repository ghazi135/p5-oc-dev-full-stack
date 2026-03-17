package com.openclassrooms.mdd_api.common.web.response;

import java.util.List;

/**
 * Réponse d'erreur standard de l'API : code, message et éventuelles erreurs de champs.
 */
public record ApiErrorResponse(
        String error,
        String message,
        List<FieldErrorItem> fieldErrors
) {}
