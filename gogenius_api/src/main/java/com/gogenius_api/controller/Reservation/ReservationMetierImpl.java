package com.gogenius_api.controller.Reservation;

import com.gogenius_api.dto.ReservationDTO.*;
import com.gogenius_api.repository.IReservationRepository;
import com.gogenius_api.repository.IUserRepository;
import com.gogenius_api.repository.entities.Reservation;
import com.gogenius_api.repository.entities.Reservation.*;
import com.gogenius_api.repository.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationMetierImpl implements IReservationMetier {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;



    /**
     * Créer une nouvelle réservation
     */
    @Override
    public ReservationResponse createReservation(String userId, CreateReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setUser(user);
        reservation.setEstablishmentName(request.getEstablishmentName());
        reservation.setType(request.getType());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setNumberOfPersons(request.getNumberOfPersons());
        reservation.setPrice(request.getPrice());
        reservation.setLocation(request.getLocation());
        reservation.setNotes(request.getNotes());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Obtenir toutes les réservations d'un utilisateur
     */
    @Override
    public List<ReservationResponse> getAllReservations(String userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les réservations avec pagination
     */
    @Override
    public Page<ReservationResponse> getReservationsPaginated(String userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return reservationRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Obtenir les 3 dernières réservations (pour le dashboard)
     */
    @Override
    public List<ReservationResponse> getLatestReservations(String userId) {
        return reservationRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les réservations à venir
     */
    @Override
    public List<ReservationResponse> getUpcomingReservations(String userId) {
        return reservationRepository.findUpcomingReservations(userId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les réservations passées
     */
    @Override
    public List<ReservationResponse> getPastReservations(String userId) {
        return reservationRepository.findPastReservations(userId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir une réservation par ID
     */
    @Override
    public Optional<ReservationResponse> getReservationById(String userId, String reservationId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId)
                .map(this::mapToResponse);
    }

    /**
     * Obtenir une réservation par code de confirmation
     */
    @Override
    public Optional<ReservationResponse> getReservationByCode(String confirmationCode) {
        return reservationRepository.findByConfirmationCode(confirmationCode)
                .map(this::mapToResponse);
    }

    /**
     * Mettre à jour une réservation
     */
    @Override
    public ReservationResponse updateReservation(String userId, String reservationId, UpdateReservationRequest request) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier que la réservation peut être modifiée
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED ||
                reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new RuntimeException("Cette réservation ne peut plus être modifiée");
        }

        // Mettre à jour les champs non nulls
        if (request.getEstablishmentName() != null) {
            reservation.setEstablishmentName(request.getEstablishmentName());
        }
        if (request.getType() != null) {
            reservation.setType(request.getType());
        }
        if (request.getReservationDate() != null) {
            reservation.setReservationDate(request.getReservationDate());
        }
        if (request.getReservationTime() != null) {
            reservation.setReservationTime(request.getReservationTime());
        }
        if (request.getNumberOfPersons() != null) {
            reservation.setNumberOfPersons(request.getNumberOfPersons());
        }
        if (request.getPrice() != null) {
            reservation.setPrice(request.getPrice());
        }
        if (request.getLocation() != null) {
            reservation.setLocation(request.getLocation());
        }
        if (request.getNotes() != null) {
            reservation.setNotes(request.getNotes());
        }

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Annuler une réservation
     */
    @Override
    public ReservationResponse cancelReservation(String userId, String reservationId, CancelReservationRequest request) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier que la réservation peut être annulée
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cette réservation est déjà annulée");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new RuntimeException("Une réservation terminée ne peut pas être annulée");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(java.time.LocalDateTime.now());
        if (request != null && request.getReason() != null) {
            reservation.setCancellationReason(request.getReason());
        }

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Confirmer une réservation (admin/partner)
     */
    @Override
    public ReservationResponse confirmReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Seules les réservations en attente peuvent être confirmées");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Marquer une réservation comme terminée
     */
    @Override
    public ReservationResponse completeReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Une réservation annulée ne peut pas être marquée comme terminée");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Supprimer une réservation
     */
    @Override
    public void deleteReservation(String userId, String reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        reservationRepository.delete(reservation);
    }

    /**
     * Obtenir les statistiques des réservations
     */
    @Override
    public ReservationStatsResponse getReservationStats(String userId) {
        ReservationStatsResponse stats = new ReservationStatsResponse();

        List<Reservation> allReservations = reservationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        stats.setTotalReservations(allReservations.size());
        stats.setPendingReservations(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.PENDING));
        stats.setConfirmedReservations(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.CONFIRMED));
        stats.setCancelledReservations(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.CANCELLED));
        stats.setCompletedReservations(reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.COMPLETED));
        stats.setUpcomingReservations(reservationRepository.findUpcomingReservations(userId, LocalDate.now()).size());

        return stats;
    }

    /**
     * Rechercher des réservations avec filtres
     */
    @Override
    public Page<ReservationResponse> searchReservations(
            String userId,
            ReservationStatus status,
            ReservationType type,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);
        return reservationRepository.findWithFilters(userId, status, type, location, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    // ==================== MAPPER ====================

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setEstablishmentName(reservation.getEstablishmentName());
        response.setType(reservation.getType().name());
        response.setTypeDisplayName(reservation.getType().getDisplayName());
        response.setReservationDate(reservation.getReservationDate());
        response.setReservationTime(reservation.getReservationTime());
        response.setNumberOfPersons(reservation.getNumberOfPersons());
        response.setPrice(reservation.getPrice());
        response.setLocation(reservation.getLocation());
        response.setStatus(reservation.getStatus().name());
        response.setStatusDisplayName(reservation.getStatus().getDisplayName());
        response.setNotes(reservation.getNotes());
        response.setConfirmationCode(reservation.getConfirmationCode());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        response.setCancelledAt(reservation.getCancelledAt());
        response.setCancellationReason(reservation.getCancellationReason());
        return response;
    }
}
