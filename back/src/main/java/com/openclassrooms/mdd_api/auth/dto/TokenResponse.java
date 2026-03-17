package com.openclassrooms.mdd_api.auth.dto;

/** Réponse login/refresh : access token JWT, type (Bearer), durée de validité en secondes. */
public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {}
