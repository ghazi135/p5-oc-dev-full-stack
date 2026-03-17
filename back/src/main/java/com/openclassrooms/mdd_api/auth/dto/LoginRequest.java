package com.openclassrooms.mdd_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Requête de connexion : identifiant (email ou username) et mot de passe. */
public record LoginRequest(
        @NotBlank @Size(max = 254) String identifier,
        @NotBlank @Size(min = 1, max = 72) String password
) {}
