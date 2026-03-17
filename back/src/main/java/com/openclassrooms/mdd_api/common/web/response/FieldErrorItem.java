package com.openclassrooms.mdd_api.common.web.response;

/** Erreur de validation sur un champ (nom du champ + message). */
public record FieldErrorItem(
        String field,
        String message
) {}
