package com.openclassrooms.mdd_api.auth.service;

import com.openclassrooms.mdd_api.auth.entity.RefreshToken;
import com.openclassrooms.mdd_api.auth.repository.RefreshTokenRepository;
import com.openclassrooms.mdd_api.common.config.OcAppProperties;
import com.openclassrooms.mdd_api.common.web.exception.ApiUnauthorizedException;
import com.openclassrooms.mdd_api.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Service de gestion des refresh tokens (stockage hashé, rotation, révocation).
 * <p>
 * Un seul refresh token actif par utilisateur (session unique). Les tokens sont stockés sous forme de hash SHA-256.
 * La rotation invalide l'ancien token et en émet un nouveau à chaque refresh.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    /** Token brut émis + date d'expiration. */
    public record Issued(String rawToken, Instant expiresAt) {}
    /** Résultat d'une rotation : userId + nouveau token émis. */
    public record Rotated(long userId, Issued issued) {}

    private static final int RAW_TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final OcAppProperties props;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Émet un nouveau refresh token pour l'utilisateur et révoque tous les anciens (une session par user).
     *
     * @param user utilisateur connecté
     * @return token brut et date d'expiration
     */
    @Transactional
    public Issued issueSingleSession(User user) {
        Instant now = Instant.now();
        refreshTokenRepository.revokeAllActiveByUserId(user.getId(), now);
        return persistNewToken(user, now);
    }

    /**
     * Rotation du refresh token : invalide l'ancien et émet un nouveau. Utilisé lors du refresh d'access token.
     *
     * @param rawToken valeur brute du cookie refresh token
     * @return userId et nouveau token émis
     * @throws ApiUnauthorizedException si le token est manquant, invalide ou expiré
     */
    @Transactional
    public Rotated rotate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ApiUnauthorizedException("Missing refresh token");
        }
        Instant now = Instant.now();
        String hash = sha256Hex(rawToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHashForUpdate(hash)
                .filter(rt -> rt.isActive(now))
                .orElseThrow(() -> new ApiUnauthorizedException("Invalid refresh token"));
        long userId = existing.getUser().getId();
        refreshTokenRepository.revokeAllActiveByUserId(userId, now);
        Issued issued = persistNewToken(existing.getUser(), now);
        return new Rotated(userId, issued);
    }

    /**
     * Révoque le refresh token (logout). No-op si token null ou vide.
     *
     * @param rawToken valeur brute du cookie refresh token à révoquer
     */
    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokenRepository.revokeByTokenHash(sha256Hex(rawToken), Instant.now());
    }

    private Issued persistNewToken(User user, Instant now) {
        String raw = generateRawToken();
        String hash = sha256Hex(raw);
        Instant expiresAt = now.plusMillis(props.getRefreshTokenExpirationMs());
        refreshTokenRepository.save(new RefreshToken(user, hash, expiresAt));
        return new Issued(raw, expiresAt);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[RAW_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
