// Fichier: ApiResponse.java
package com.gogenius_api.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Format de réponse conforme à la spec PDF :
 * - code : code crypté en MD5 ("success", "failed", "Error")
 * - message : optionnel (seulement pour les erreurs)
 * - data : les données de réponse (token, user_id, etc.)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String code;        // Code crypté en MD5
    private String message;     // Optionnel (seulement pour erreurs)
    private Object data;        // Données de réponse

    // Constructeurs
    public ApiResponse() {}

    public ApiResponse(String code) {
        this.code = code;
    }

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String code, Object data) {
        this.code = code;
        this.data = data;
    }

    // Getters et Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Succès : code "success" crypté en MD5 + token + data
     * PAS de message pour les succès (selon spec PDF)
     */
    public static ApiResponse success(Object data) {
        return new ApiResponse(hashMd5("success"), null, data);
    }

    /**
     * Échec validation : code "failed" crypté en MD5 
     * (PAS de message pour les failed selon spec)
     */
    public static ApiResponse failed() {
        return new ApiResponse(hashMd5("failed"), null, null);
    }

    /**
     * Erreur technique : code "Error" crypté en MD5 + message
     * Message SEULEMENT pour les erreurs (selon spec PDF)
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(hashMd5("Error"), message, null);
    }

    // ==================== CRYPTAGE MD5 ====================

    /**
     * Crypte une chaîne en MD5
     */
    public static String hashMd5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return input; // Fallback
        }
    }
}