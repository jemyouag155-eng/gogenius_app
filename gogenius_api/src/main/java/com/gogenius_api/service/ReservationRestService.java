package com.gogenius_api.service;

import com.gogenius_api.controller.Reservation.IReservationMetier;
import com.gogenius_api.repository.entities.Reservation.*;
import com.gogenius_api.dto.ReservationDTO.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationRestService {

    @Autowired
    private IReservationMetier reservationService;

    // ==================== ENDPOINTS UTILISATEUR ====================

    /**
     * Créer une nouvelle réservation
     * POST /api/reservations
     */
    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateReservationRequest request) {
        try {
            ReservationResponse response = reservationService.createReservation(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Obtenir toutes les réservations de l'utilisateur
     * GET /api/reservations
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(
            @RequestHeader("X-User-Id") String userId) {
        List<ReservationResponse> reservations = reservationService.getAllReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtenir les réservations avec pagination
     * GET /api/reservations/paginated?page=0&size=10&sortBy=createdAt&sortDir=desc
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<ReservationResponse>> getReservationsPaginated(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<ReservationResponse> reservations = reservationService.getReservationsPaginated(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtenir les 3 dernières réservations (pour le dashboard)
     * GET /api/reservations/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ReservationResponse>> getLatestReservations(
            @RequestHeader("X-User-Id") String userId) {
        List<ReservationResponse> reservations = reservationService.getLatestReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtenir les réservations à venir
     * GET /api/reservations/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ReservationResponse>> getUpcomingReservations(
            @RequestHeader("X-User-Id") String userId) {
        List<ReservationResponse> reservations = reservationService.getUpcomingReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtenir les réservations passées
     * GET /api/reservations/past
     */
    @GetMapping("/past")
    public ResponseEntity<List<ReservationResponse>> getPastReservations(
            @RequestHeader("X-User-Id") String userId) {
        List<ReservationResponse> reservations = reservationService.getPastReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtenir une réservation par ID
     * GET /api/reservations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        return reservationService.getReservationById(userId, id)
                .map(reservation -> ResponseEntity.ok((Object) reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtenir une réservation par code de confirmation
     * GET /api/reservations/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getReservationByCode(@PathVariable String code) {
        return reservationService.getReservationByCode(code)
                .map(reservation -> ResponseEntity.ok((Object) reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour une réservation
     * PUT /api/reservations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id,
            @Valid @RequestBody UpdateReservationRequest request) {
        try {
            ReservationResponse response = reservationService.updateReservation(userId, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Annuler une réservation
     * POST /api/reservations/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id,
            @RequestBody(required = false) CancelReservationRequest request) {
        try {
            ReservationResponse response = reservationService.cancelReservation(userId, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Supprimer une réservation
     * DELETE /api/reservations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        try {
            reservationService.deleteReservation(userId, id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Réservation supprimée avec succès"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Obtenir les statistiques des réservations
     * GET /api/reservations/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ReservationStatsResponse> getReservationStats(
            @RequestHeader("X-User-Id") String userId) {
        ReservationStatsResponse stats = reservationService.getReservationStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Rechercher des réservations avec filtres
     * GET /api/reservations/search?status=CONFIRMED&type=HOTEL&location=Marrakech&startDate=2026-01-01&endDate=2026-12-31
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ReservationResponse>> searchReservations(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) ReservationType type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReservationResponse> results = reservationService.searchReservations(
                userId, status, type, location, startDate, endDate, page, size);
        return ResponseEntity.ok(results);
    }

    // ==================== ENDPOINTS ADMIN/PARTNER ====================

    /**
     * Confirmer une réservation (admin/partner)
     * POST /api/reservations/{id}/confirm
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmReservation(@PathVariable String id) {
        try {
            ReservationResponse response = reservationService.confirmReservation(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Marquer une réservation comme terminée
     * POST /api/reservations/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeReservation(@PathVariable String id) {
        try {
            ReservationResponse response = reservationService.completeReservation(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    // ==================== ENDPOINTS UTILITAIRES ====================

    /**
     * Obtenir les types de réservation disponibles
     * GET /api/reservations/types
     */
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getReservationTypes() {
        List<Map<String, String>> types = java.util.Arrays.stream(ReservationType.values())
                .map(type -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("value", type.name());
                    map.put("label", type.getDisplayName());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(types);
    }

    /**
     * Obtenir les statuts de réservation disponibles
     * GET /api/reservations/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<Map<String, String>>> getReservationStatuses() {
        List<Map<String, String>> statuses = java.util.Arrays.stream(ReservationStatus.values())
                .map(status -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("value", status.name());
                    map.put("label", status.getDisplayName());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(statuses);
    }
}
