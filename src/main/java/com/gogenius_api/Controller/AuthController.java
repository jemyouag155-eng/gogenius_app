package com.gogenius_api.Controller;

import com.gogenius_api.Model.User;
import com.gogenius_api.Service.EmailService;
import com.gogenius_api.Service.JwtService;
import com.gogenius_api.Service.TokenBlacklistService;
import com.gogenius_api.auth.dto.*;
import com.gogenius_api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UserRepository repository,
            PasswordEncoder encoder,
            JwtService jwtService,
            EmailService emailService,
            TokenBlacklistService tokenBlacklistService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // ==================== INSCRIPTION (SignUp) ====================
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            // RG_V_01: Vérifier tous les champs obligatoires
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            if (request.getUserType() == null || request.getUserType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }        
            //  Utiliser la méthode décodée 

            if (request.isTermsAcceptedDecoded() == false) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            String decodedUserType;
            try {
                decodedUserType = request.getUserTypeDecoded();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            
            if (!decodedUserType.equals("tourist") && !decodedUserType.equals("partner")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            // RG_V_01: Si partner, la company est obligatoire
            if (decodedUserType.equals("partner") && 
                (request.getCompany() == null || request.getCompany().trim().isEmpty())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }


            // RG_V_02: Vérifier que l'email n'existe pas déjà
            if (repository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            // RG_V_02: Vérifier que le login n'existe pas déjà
            if (repository.findByLogin(request.getLogin()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            // RG_V_03: Créer l'utilisateur avec UUID auto-généré
            String userId = UUID.randomUUID().toString();
            User user = User.builder()
                    .id(userId)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .login(request.getLogin())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getPassword()))
                    .userType(decodedUserType) 
                    .termsAccepted(request.isTermsAcceptedDecoded()) 
                    .company(request.getCompany())
                    .build();

            repository.save(user);

            // RG_V_06: Générer le token avec tous les claims requis
            String token = jwtService.generateToken(userId, request.getEmail(), null, false);

            // Retourner la réponse avec code "success" en MD5
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("user_id", userId);

            return ResponseEntity.ok(ApiResponse.success(responseData));

        } catch (Exception e) {
            // Erreur technique = code "Error" en MD5 + message
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }
  
 // ==================== AUTHENTIFICATION (SignIn) ====================
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
    	
        try {
            // Utiliser la méthode isValid() pour vérifier qu'au moins un champ est fourni
            if (!request.isValid()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            // Déterminer quel identifiant utiliser
            String identifier;
            boolean isEmail = false;
            
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                // Si email fourni, l'utiliser
                identifier = request.getEmail().trim();
                isEmail = true;
            } else {
                // Sinon utiliser le login
                identifier = request.getLogin().trim();
            }

            // Chercher par email OU login
            User user = repository.findByEmailOrLoginIgnoreCase(identifier)
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            // Vérifier le mot de passe
            if (!encoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            if (isEmail && !identifier.equalsIgnoreCase(user.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }
            boolean keepSession = request.decodeKeepSession();
            
            // RG_V_03: Gérer keepSession avec la valeur décodée
            if (!keepSession) {
                user.setLastActivity(LocalDateTime.now());
                repository.save(user);
            }

            // Générer le token avec keepSession
            String token = jwtService.generateToken(user.getId(), user.getEmail(), null,keepSession);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("user_id", user.getId());
            responseData.put("email", user.getEmail());
            responseData.put("login", user.getLogin());

            return ResponseEntity.ok(ApiResponse.success(responseData));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }
    // ==================== LOGOUT ====================
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            String token = authHeader.substring(7);

            if (token.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            if (!jwtService.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            if (tokenBlacklistService.isBlacklisted(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            tokenBlacklistService.addToBlacklist(token);

            return ResponseEntity.ok(ApiResponse.success(null));

        } catch (StringIndexOutOfBoundsException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failed());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    // ==================== MOT DE PASSE OUBLIÉ ====================
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email non trouvé"));

            String resetToken = String.format("%06d", (int) (Math.random() * 1000000));

            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            repository.save(user);

            emailService.sendResetPasswordEmail(user.getEmail(), resetToken);

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    // ==================== RÉINITIALISER LE MOT DE PASSE ====================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            User user = repository.findByResetToken(request.getToken())
                    .orElseThrow(() -> new RuntimeException("Token invalide"));

            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            user.setPassword(encoder.encode(request.getNewPassword()));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            repository.save(user);

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }

    // ==================== ENDPOINT DE TEST ====================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            String token = authHeader.substring(7);

            if (!jwtService.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.failed());
            }

            String email = jwtService.extractEmail(token);
            String userId = jwtService.extractUserId(token);

            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", userId);
            userData.put("email", email);

            return ResponseEntity.ok(ApiResponse.success(userData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erreur serveur: " + e.getMessage()));
        }
    }
}