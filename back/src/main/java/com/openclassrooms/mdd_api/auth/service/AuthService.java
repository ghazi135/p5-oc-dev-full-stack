package com.openclassrooms.mdd_api.auth.service;

import com.openclassrooms.mdd_api.auth.dto.LoginRequest;
import com.openclassrooms.mdd_api.auth.dto.RegisterRequest;
import com.openclassrooms.mdd_api.auth.dto.TokenResponse;
import com.openclassrooms.mdd_api.auth.validation.PasswordPolicy;
import com.openclassrooms.mdd_api.common.web.exception.ApiBadRequestException;
import com.openclassrooms.mdd_api.common.web.exception.ApiConflictException;
import com.openclassrooms.mdd_api.common.web.exception.ApiUnauthorizedException;
import com.openclassrooms.mdd_api.common.web.response.FieldErrorItem;
import com.openclassrooms.mdd_api.user.entity.User;
import com.openclassrooms.mdd_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service d'authentification : inscription, connexion, refresh et déconnexion.
 * <p>
 * Gère la création de comptes (avec politique de mot de passe), l'émission des JWT et des refresh tokens,
 * et la rotation/révocation des refresh tokens.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /** Réponse token (access + refresh brut) retournée par login et refresh. */
    public record TokenBundle(TokenResponse tokenResponse, String refreshTokenRaw) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private static String normalizeIdentifier(String identifier) {
        return identifier == null ? null : identifier.trim();
    }

    /**
     * Inscrit un nouvel utilisateur (email, username, mot de passe).
     *
     * @param req données d'inscription
     * @return l'identifiant du compte créé
     * @throws ApiBadRequestException si le mot de passe ne respecte pas la politique
     * @throws ApiConflictException   si l'email ou le username est déjà utilisé
     */
    @Transactional
    public Long register(RegisterRequest req) {
        if (!PasswordPolicy.isValid(req.password())) {
            throw new ApiBadRequestException(
                    "Password policy not respected",
                    List.of(new FieldErrorItem("password",
                            "Must be >=8 and contain lower, upper, digit and special character"))
            );
        }
        String email = normalizeEmail(req.email());
        String username = normalizeUsername(req.username());
        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
            throw new ApiConflictException("Email or username already used");
        }
        try {
            User u = new User(email, username, passwordEncoder.encode(req.password()));
            return userRepository.save(u).getId();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Email or username already used");
        }
    }

    /**
     * Authentifie un utilisateur par email ou username + mot de passe et émet les tokens.
     *
     * @param req identifiant (email ou username) et mot de passe
     * @return access token + refresh token brut
     * @throws BadCredentialsException si les identifiants sont invalides
     */
    @Transactional
    public TokenBundle login(LoginRequest req) {
        String identifier = normalizeIdentifier(req.identifier());
        String emailCandidate = normalizeEmail(identifier);
        User user = userRepository.findByEmail(emailCandidate)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        TokenResponse access = jwtService.issueAccessToken(user);
        var refresh = refreshTokenService.issueSingleSession(user);
        return new TokenBundle(access, refresh.rawToken());
    }

    /**
     * Renouvelle l'access token à partir d'un refresh token valide (rotation du refresh token).
     *
     * @param refreshTokenRaw valeur brute du cookie refresh token
     * @return nouvel access token + nouveau refresh token brut
     * @throws ApiUnauthorizedException si le refresh token est manquant, invalide ou expiré
     */
    @Transactional
    public TokenBundle refresh(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new ApiUnauthorizedException("Missing refresh token");
        }
        var rotated = refreshTokenService.rotate(refreshTokenRaw);
        User user = userRepository.findById(rotated.userId())
                .orElseThrow(() -> new ApiUnauthorizedException("Invalid refresh token"));
        TokenResponse access = jwtService.issueAccessToken(user);
        return new TokenBundle(access, rotated.issued().rawToken());
    }

    /**
     * Déconnecte l'utilisateur en révoquant le refresh token.
     *
     * @param refreshTokenRaw valeur brute du cookie refresh token à révoquer
     * @throws ApiUnauthorizedException si le refresh token est manquant ou vide
     */
    @Transactional
    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new ApiUnauthorizedException("Missing refresh token");
        }
        refreshTokenService.revoke(refreshTokenRaw);
    }
}
