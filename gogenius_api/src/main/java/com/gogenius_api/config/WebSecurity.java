package com.gogenius_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Date;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF (inutile pour les API Stateless avec Token)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Activer la gestion CORS par défaut
                .cors(Customizer.withDefaults())

                // 3. Gérer les permissions
                .authorizeHttpRequests(auth -> auth
                        // Autoriser l'accès public au endpoint de login
                        .requestMatchers("/v1/signIn").permitAll()
                        // Autoriser explicitement les requêtes OPTIONS (Preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                )
                // Désactiver le formulaire de login par défaut (car c'est une API REST)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // 4. Définir la configuration CORS Globale
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriser votre frontend Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        // Autoriser les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Autoriser les headers (Content-Type, Authorization, etc.)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Autoriser l'envoi de credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}