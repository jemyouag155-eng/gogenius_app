package com.gogenius_api.service;

import com.gogenius_api.Model.auth.ForgotPasswordRequest;
import com.gogenius_api.Model.auth.LoginRequest;
import com.gogenius_api.Model.auth.RegisterRequest;
import com.gogenius_api.Model.auth.ResetPasswordRequest;
import com.gogenius_api.Model.responses.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.gogenius_api.controller.Users.IUserMetier;
import com.gogenius_api.helper.AuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class UserRestService {

    private IUserMetier userMetier;


    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> result = userMetier.register(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (AuthException e) {
            return handleAuthException(e);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userMetier.login(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (AuthException e) {
            return handleAuthException(e);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = userMetier.extractTokenFromHeader(authHeader);
            userMetier.logout(token);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userMetier.forgotPassword(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (AuthException e) {
            return handleAuthException(e);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userMetier.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (AuthException e) {
            return handleAuthException(e);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = userMetier.extractTokenFromHeader(authHeader);
            Map<String, Object> userData = userMetier.getCurrentUser(token);
            return ResponseEntity.ok(ApiResponse.success(userData));
        } catch (AuthException e) {
            return handleAuthException(e);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    // ==================== GESTION DES ERREURS ====================

    private ResponseEntity<ApiResponse> handleAuthException(AuthException e) {
        return switch (e.getErrorType()) {
            case VALIDATION_ERROR, USER_EXISTS, USER_NOT_FOUND,
                 INVALID_CREDENTIALS, INVALID_TOKEN, TOKEN_BLACKLISTED ->
                    ResponseEntity.badRequest().body(ApiResponse.failed());
            case TECHNICAL_ERROR ->
                    ResponseEntity.internalServerError()
                            .body(ApiResponse.error(e.getMessage()));
        };
    }
}
