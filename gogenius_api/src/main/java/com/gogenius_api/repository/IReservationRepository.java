package com.gogenius_api.repository;

import com.gogenius_api.repository.entities.Reservation;
import com.gogenius_api.repository.entities.Reservation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, String> {

    // Trouver toutes les réservations d'un utilisateur
    List<Reservation> findByUserIdOrderByCreatedAtDesc(String userId);

    // Trouver les réservations d'un utilisateur avec pagination
    Page<Reservation> findByUserId(String userId, Pageable pageable);

    // Trouver les N dernières réservations d'un utilisateur
    List<Reservation> findTop3ByUserIdOrderByCreatedAtDesc(String userId);

    // Trouver les réservations par statut
    List<Reservation> findByUserIdAndStatusOrderByReservationDateAsc(String userId, ReservationStatus status);

    // Trouver les réservations par type
    List<Reservation> findByUserIdAndTypeOrderByReservationDateDesc(String userId, ReservationType type);

    // Trouver les réservations à venir
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.reservationDate >= :today ORDER BY r.reservationDate ASC")
    List<Reservation> findUpcomingReservations(@Param("userId") String userId, @Param("today") LocalDate today);

    // Trouver les réservations passées
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.reservationDate < :today ORDER BY r.reservationDate DESC")
    List<Reservation> findPastReservations(@Param("userId") String userId, @Param("today") LocalDate today);

    // Trouver par code de confirmation
    Optional<Reservation> findByConfirmationCode(String confirmationCode);

    // Trouver par ID et userId (sécurité)
    Optional<Reservation> findByIdAndUserId(String id, String userId);

    // Compter les réservations par statut pour un utilisateur
    long countByUserIdAndStatus(String userId, ReservationStatus status);

    // Recherche avec filtres multiples
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:type IS NULL OR r.type = :type) " +
            "AND (:location IS NULL OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:startDate IS NULL OR r.reservationDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.reservationDate <= :endDate) " +
            "ORDER BY r.reservationDate DESC")
    Page<Reservation> findWithFilters(
            @Param("userId") String userId,
            @Param("status") ReservationStatus status,
            @Param("type") ReservationType type,
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // Statistiques - Nombre total de réservations par mois
    @Query("SELECT MONTH(r.createdAt) as month, COUNT(r) as count " +
            "FROM Reservation r WHERE r.user.id = :userId AND YEAR(r.createdAt) = :year " +
            "GROUP BY MONTH(r.createdAt)")
    List<Object[]> countReservationsByMonth(@Param("userId") String userId, @Param("year") int year);
}