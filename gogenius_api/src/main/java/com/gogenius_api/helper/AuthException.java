package com.gogenius_api.helper;

public class AuthException extends Exception{
    public enum ErrorType {
        VALIDATION_ERROR,      // Erreur de validation des champs
        USER_EXISTS,           // Utilisateur déjà existant
        USER_NOT_FOUND,        // Utilisateur non trouvé
        INVALID_CREDENTIALS,   // Identifiants invalides
        INVALID_TOKEN,         // Token invalide ou expiré
        TOKEN_BLACKLISTED,     // Token déjà révoqué
        TECHNICAL_ERROR        // Erreur technique
    }

    private final ErrorType errorType;

    public AuthException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public AuthException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
