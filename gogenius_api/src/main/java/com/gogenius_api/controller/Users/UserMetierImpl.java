package com.gogenius_api.controller.Users;

import com.gogenius_api.Model.auth.LoginRequest;
import com.gogenius_api.Model.auth.RegisterRequest;
import com.gogenius_api.repository.IUserRepository;

import com.gogenius_api.repository.entities.User;
import com.gogenius_api.service.helpers.EmailService;
import com.gogenius_api.service.helpers.JwtService;
import com.gogenius_api.service.helpers.TokenBlacklistService;
import com.gogenius_api.helper.AuthException;
import com.gogenius_api.helper.AuthException.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserMetierImpl implements IUserMetier{
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public Map<String, Object> register(RegisterRequest request) throws AuthException {
        // RG_V_01: Validation des champs obligatoires
        validateRegisterRequest(request);

        // Décoder et valider le userType
        String decodedUserType = decodeAndValidateUserType(request);

        // RG_V_01: Si partner, la company est obligatoire
        if (decodedUserType.equals("partner") && isBlank(request.getCompany())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Company obligatoire pour les partners");
        }

        // RG_V_02: Vérifier l'unicité de l'email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException(ErrorType.USER_EXISTS, "Email déjà utilisé");
        }

        // RG_V_02: Vérifier l'unicité du login
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new AuthException(ErrorType.USER_EXISTS, "Login déjà utilisé");
        }

        // RG_V_03: Créer l'utilisateur
        String userId = UUID.randomUUID().toString();
        User user = User.builder()
                .id(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .login(request.getLogin())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(decodedUserType)
                .termsAccepted(request.isTermsAcceptedDecoded())
                .company(request.getCompany())
                .build();

        userRepository.save(user);

        // RG_V_06: Générer le token
        String token = jwtService.generateToken(userId, request.getEmail(), null, false);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("user_id", userId);

        return responseData;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) throws AuthException {
        // Validation des champs
        if (!request.isValid()) {
            throw new AuthException(com.gogenius_api.helper.AuthException.ErrorType.VALIDATION_ERROR, "Email ou login requis");
        }

        if (isBlank(request.getPassword())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Mot de passe requis");
        }

        // Déterminer l'identifiant
        String identifier;
        boolean isEmail = false;

        if (!isBlank(request.getEmail())) {
            identifier = request.getEmail().trim();
            isEmail = true;
        } else {
            identifier = request.getLogin().trim();
        }

        // Rechercher l'utilisateur
        User user = userRepository.findByEmailOrLoginIgnoreCase(identifier);

        // Vérifier le mot de passe
        if (request.getPassword().equals(user.getPassword())) {
            throw new AuthException(ErrorType.INVALID_CREDENTIALS, "Mot de passe incorrect");
        }

        // Vérification supplémentaire pour l'email
        if (isEmail && !identifier.equalsIgnoreCase(user.getEmail())) {
            throw new AuthException(ErrorType.INVALID_CREDENTIALS, "Email ne correspond pas");
        }

        boolean keepSession = request.decodeKeepSession();

        // RG_V_03: Gérer keepSession
        if (!keepSession) {
            user.setLastActivity(LocalDateTime.now());
            userRepository.save(user);
        }

        // Générer le token
        String token = jwtService.generateToken(user.getId(), user.getEmail(), null, keepSession);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("user_id", user.getId());
        responseData.put("email", user.getEmail());
        responseData.put("login", user.getLogin());

        return responseData;
    }

    @Override
    public void logout(String token) throws AuthException {
        // Valider le token
        if (!jwtService.validateToken(token)) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Token invalide");
        }

        // Vérifier si déjà blacklisté
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new AuthException(ErrorType.TOKEN_BLACKLISTED, "Token déjà révoqué");
        }

        // Ajouter à la blacklist
        tokenBlacklistService.addToBlacklist(token);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorType.USER_NOT_FOUND, "Email non trouvé"));

        // Générer le token de reset
        String resetToken = String.format("%06d", (int) (Math.random() * 1000000));

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Envoyer l'email
        emailService.sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws AuthException {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new AuthException(ErrorType.INVALID_TOKEN, "Token invalide"));

        // Vérifier l'expiration
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Token expiré");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public Map<String, Object> getCurrentUser(String token) throws AuthException {
        if (!jwtService.validateToken(token)) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Token invalide");
        }

        String email = jwtService.extractEmail(token);
        String userId = jwtService.extractUserId(token);

        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", userId);
        userData.put("email", email);

        return userData;
    }

    @Override
    public String extractTokenFromHeader(String authHeader) throws AuthException {
        if (authHeader == null || authHeader.isEmpty()) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Header Authorization manquant");
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Format Bearer invalide");
        }

        String token = authHeader.substring(7);

        if (token.isEmpty()) {
            throw new AuthException(ErrorType.INVALID_TOKEN, "Token vide");
        }

        return token;
    }

    // ==================== MÉTHODES PRIVÉES ====================

    private void validateRegisterRequest(RegisterRequest request) throws AuthException {
        if (isBlank(request.getFirstName())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Prénom obligatoire");
        }
        if (isBlank(request.getLastName())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Nom obligatoire");
        }
        if (isBlank(request.getLogin())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Login obligatoire");
        }
        if (isBlank(request.getEmail())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Email obligatoire");
        }
        if (isBlank(request.getPassword())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Mot de passe obligatoire");
        }
        if (isBlank(request.getUserType())) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Type utilisateur obligatoire");
        }
        if (!request.isTermsAcceptedDecoded()) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "Conditions d'utilisation non acceptées");
        }
    }

    private String decodeAndValidateUserType(RegisterRequest request) throws AuthException {
        String decodedUserType;
        try {
            decodedUserType = request.getUserTypeDecoded();
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "UserType invalide");
        }

        if (!decodedUserType.equals("tourist") && !decodedUserType.equals("partner")) {
            throw new AuthException(ErrorType.VALIDATION_ERROR, "UserType doit être 'tourist' ou 'partner'");
        }

        return decodedUserType;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
