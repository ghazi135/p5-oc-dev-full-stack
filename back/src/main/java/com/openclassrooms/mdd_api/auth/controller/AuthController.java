package com.openclassrooms.mdd_api.auth.controller;

import com.openclassrooms.mdd_api.auth.dto.LoginRequest;
import com.openclassrooms.mdd_api.auth.dto.RegisterRequest;
import com.openclassrooms.mdd_api.auth.dto.TokenResponse;
import com.openclassrooms.mdd_api.auth.service.AuthService;
import com.openclassrooms.mdd_api.common.config.OcAppProperties;
import com.openclassrooms.mdd_api.common.web.exception.ApiUnauthorizedException;
import com.openclassrooms.mdd_api.common.web.response.IdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Tag(name = "Auth", description = "Authentication endpoints (JWT access + refresh cookie HttpOnly + CSRF)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String ACCESS_COOKIE_NAME = "accessToken";
    private static final String XSRF_HEADER = "X-XSRF-TOKEN";

    private final AuthService authService;
    private final OcAppProperties props;

    @Operation(summary = "Init CSRF cookie (SPA)")
    @ApiResponse(responseCode = "204", description = "CSRF cookie issued (XSRF-TOKEN)")
    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(
            @Parameter(hidden = true) @RequestAttribute(name = "_csrf", required = false) CsrfToken token
    ) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Register (email, username, password)")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Validation error / password policy")
    @ApiResponse(responseCode = "409", description = "Email/username already used")
    @ApiResponse(responseCode = "403", description = "CSRF missing/invalid")
    @PostMapping("/register")
    public ResponseEntity<IdResponse> register(
            @RequestHeader(name = XSRF_HEADER, required = false) String xsrfToken,
            @Valid @RequestBody RegisterRequest request
    ) {
        Long id = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @Operation(summary = "Login with email or username")
    @ApiResponse(responseCode = "200", description = "Access token issued + refresh cookie set")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "403", description = "CSRF missing/invalid")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestHeader(name = XSRF_HEADER, required = false) String xsrfToken,
            @Valid @RequestBody LoginRequest request
    ) {
        var bundle = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(SET_COOKIE, buildRefreshCookie(bundle.refreshTokenRaw()).toString());
        headers.add(SET_COOKIE, buildAccessCookie(bundle.tokenResponse().accessToken(), bundle.tokenResponse().expiresInSeconds()).toString());
        return ResponseEntity.ok().headers(headers).body(bundle.tokenResponse());
    }

    @Operation(summary = "Refresh access token (refresh cookie + CSRF)")
    @ApiResponse(responseCode = "200", description = "New access token issued + refresh cookie rotated")
    @ApiResponse(responseCode = "401", description = "Refresh token invalid/expired/missing")
    @ApiResponse(responseCode = "403", description = "CSRF missing/invalid")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestHeader(name = XSRF_HEADER, required = false) String xsrfToken,
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiUnauthorizedException("Missing refresh token");
        }
        var bundle = authService.refresh(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(SET_COOKIE, buildRefreshCookie(bundle.refreshTokenRaw()).toString());
        headers.add(SET_COOKIE, buildAccessCookie(bundle.tokenResponse().accessToken(), bundle.tokenResponse().expiresInSeconds()).toString());
        return ResponseEntity.ok().headers(headers).body(bundle.tokenResponse());
    }

    @Operation(summary = "Logout (invalidate refresh token)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Logged out + refresh cookie deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "CSRF missing/invalid")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = XSRF_HEADER, required = false) String xsrfToken,
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        authService.logout(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(SET_COOKIE, deleteRefreshCookie().toString());
        headers.add(SET_COOKIE, deleteAccessCookie().toString());
        return ResponseEntity.noContent().headers(headers).build();
    }

    private ResponseCookie buildAccessCookie(String accessToken, long expiresInSeconds) {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, accessToken)
                .httpOnly(true).secure(props.isCookieSecure()).sameSite("Lax").path("/api").maxAge(expiresInSeconds).build();
    }

    private ResponseCookie buildRefreshCookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true).secure(props.isCookieSecure()).sameSite("Lax").path("/api/auth")
                .maxAge(props.getRefreshTokenExpirationMs() / 1000).build();
    }

    private ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "").httpOnly(true).secure(props.isCookieSecure()).sameSite("Lax").path("/api/auth").maxAge(0).build();
    }

    private ResponseCookie deleteAccessCookie() {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, "").httpOnly(true).secure(props.isCookieSecure()).sameSite("Lax").path("/api").maxAge(0).build();
    }
}
