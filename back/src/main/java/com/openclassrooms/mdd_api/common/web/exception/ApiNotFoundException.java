package com.openclassrooms.mdd_api.common.web.exception;

/**
 * Exception métier pour une ressource introuvable (HTTP 404).
 */
public class ApiNotFoundException extends RuntimeException {
    public ApiNotFoundException(String message) {

        super(message);
    }
}
