package com.openclassrooms.mdd_api.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Point de contrôle technique : GET /api/health pour vérifier que l'API est en ligne.
 */
@Tag(name = "Health", description = "Technical endpoints")
@RestController
public class HealthController {

    @Operation(summary = "Health check")
    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
