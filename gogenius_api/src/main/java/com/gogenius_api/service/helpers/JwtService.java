// Fichier: JwtService.java
package com.gogenius_api.service.helpers;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.secret}")
    private String secret;

    // 15 jours en millisecondes = RG_V_06
    private static final long EXPIRATION_TIME = 15L * 24 * 60 * 60 * 1000;
    
    // Format ISO 8601 pour LocalDateTime
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Génère un token JWT avec tous les claims requis (RG_V_06)
     * Claims:
     * - user_key: UUID de l'utilisateur
     * - part_profile: UUID du profil partenaire (si applicable)
     * - lst_activity: date de dernière activité (en String ISO 8601)
     * - iat: date de génération
     * - exp: date d'expiration (15 jours)
     */
    public String generateToken(String userId, String email, String partProfile, boolean keepSession) {
        // Convertir LocalDateTime en String ISO 8601
        String lstActivity = keepSession ? null : LocalDateTime.now().format(DATE_FORMATTER);

        JwtBuilder builder = Jwts.builder()
                .claim("user_key", userId)                    // RG_V_06
                .claim("email", email)
                .claim("part_profile", partProfile)           // RG_V_06
                .setIssuedAt(new Date())                      // RG_V_06
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // RG_V_06 (15 jours)
                .signWith(getKey(), SignatureAlgorithm.HS256);
        
        // Ajouter lst_activity seulement s'il n'est pas null
        if (lstActivity != null) {
            builder.claim("lst_activity", lstActivity);       // RG_V_06 & RG_V_03
        }
        
        return builder.compact();
    }

    /**
     * Génère un token simple (surcharge)
     */
    public String generateToken(String userId, String email) {
        return generateToken(userId, email, null, false);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("user_key", String.class));
    }
    
    public String extractLstActivity(String token) {
        return extractClaim(token, claims -> claims.get("lst_activity", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Erreur extraction claims: " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            System.out.println("⚠️ Erreur expiration: " + e.getMessage());
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("⚫ Token en blacklist");
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);

            System.out.println("✅ Token valide");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Token invalide: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token, String email) {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("⚫ Token en blacklist");
                return false;
            }

            String extractedEmail = extractEmail(token);
            boolean isValid = extractedEmail != null
                    && extractedEmail.equals(email)
                    && !isTokenExpired(token);

            if (isValid) {
                System.out.println("✅ Token valide pour: " + email);
            }
            return isValid;
        } catch (Exception e) {
            System.out.println("❌ Erreur vérification token: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        return validateToken(token);
    }
}