package com.gogenius_api.controller.Users;

import com.gogenius_api.Model.auth.LoginRequest;
import com.gogenius_api.Model.auth.RegisterRequest;
import com.gogenius_api.helper.AuthException;

import java.util.Map;

public interface IUserMetier {

    //Inscription d'un nouvel utilisateur
    Map<String, Object> register(RegisterRequest request) throws AuthException, com.gogenius_api.helper.AuthException;

    /**
     * Authentification d'un utilisateur
     */
    Map<String, Object> login(LoginRequest request) throws AuthException, com.gogenius_api.helper.AuthException;

    /**
     * Déconnexion d'un utilisateur
     */
    void logout(String token) throws AuthException;

    /**
     * Demande de réinitialisation de mot de passe
     */
    void forgotPassword(String email) throws AuthException;

    /**
     * Réinitialisation du mot de passe
     */
    void resetPassword(String token, String newPassword) throws AuthException;

    /**
     * Récupérer les informations de l'utilisateur courant
     */
    Map<String, Object> getCurrentUser(String token) throws AuthException;

    /**
     * Valider et extraire le token du header Authorization
     */
    String extractTokenFromHeader(String authHeader) throws AuthException;
}
