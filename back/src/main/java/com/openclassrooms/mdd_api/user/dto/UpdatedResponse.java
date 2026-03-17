package com.openclassrooms.mdd_api.user.dto;

/** Réponse PUT /api/users/me : indique si le profil a été modifié. */
public record UpdatedResponse(boolean updated) {}
