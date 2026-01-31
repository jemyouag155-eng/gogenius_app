package com.gogenius_api.dto;

import com.gogenius_api.repository.entities.Reservation.ReservationStatus;
import com.gogenius_api.repository.entities.Reservation.ReservationType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationDTO {

    // ==================== REQUEST DTOs ====================

    /**
     * DTO pour créer une nouvelle réservation
     */
    public static class CreateReservationRequest {
        
        @NotBlank(message = "Le nom de l'établissement est requis")
        @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
        private String establishmentName;
        
        @NotNull(message = "Le type de réservation est requis")
        private ReservationType type;
        
        @NotNull(message = "La date est requise")
        @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
        private LocalDate reservationDate;
        
        @NotNull(message = "L'heure est requise")
        private LocalTime reservationTime;
        
        @NotNull(message = "Le nombre de personnes est requis")
        @Min(value = 1, message = "Au moins 1 personne requise")
        @Max(value = 100, message = "Maximum 100 personnes")
        private Integer numberOfPersons;
        
        @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
        private BigDecimal price;
        
        @NotBlank(message = "La ville est requise")
        @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
        private String location;
        
        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        private String notes;

        // Getters et Setters
        public String getEstablishmentName() {
            return establishmentName;
        }

        public void setEstablishmentName(String establishmentName) {
            this.establishmentName = establishmentName;
        }

        public ReservationType getType() {
            return type;
        }

        public void setType(ReservationType type) {
            this.type = type;
        }

        public LocalDate getReservationDate() {
            return reservationDate;
        }

        public void setReservationDate(LocalDate reservationDate) {
            this.reservationDate = reservationDate;
        }

        public LocalTime getReservationTime() {
            return reservationTime;
        }

        public void setReservationTime(LocalTime reservationTime) {
            this.reservationTime = reservationTime;
        }

        public Integer getNumberOfPersons() {
            return numberOfPersons;
        }

        public void setNumberOfPersons(Integer numberOfPersons) {
            this.numberOfPersons = numberOfPersons;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    /**
     * DTO pour mettre à jour une réservation
     */
    public static class UpdateReservationRequest {
        
        @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
        private String establishmentName;
        
        private ReservationType type;
        
        @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
        private LocalDate reservationDate;
        
        private LocalTime reservationTime;
        
        @Min(value = 1, message = "Au moins 1 personne requise")
        @Max(value = 100, message = "Maximum 100 personnes")
        private Integer numberOfPersons;
        
        @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
        private BigDecimal price;
        
        @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
        private String location;
        
        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        private String notes;

        // Getters et Setters
        public String getEstablishmentName() {
            return establishmentName;
        }

        public void setEstablishmentName(String establishmentName) {
            this.establishmentName = establishmentName;
        }

        public ReservationType getType() {
            return type;
        }

        public void setType(ReservationType type) {
            this.type = type;
        }

        public LocalDate getReservationDate() {
            return reservationDate;
        }

        public void setReservationDate(LocalDate reservationDate) {
            this.reservationDate = reservationDate;
        }

        public LocalTime getReservationTime() {
            return reservationTime;
        }

        public void setReservationTime(LocalTime reservationTime) {
            this.reservationTime = reservationTime;
        }

        public Integer getNumberOfPersons() {
            return numberOfPersons;
        }

        public void setNumberOfPersons(Integer numberOfPersons) {
            this.numberOfPersons = numberOfPersons;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    /**
     * DTO pour annuler une réservation
     */
    public static class CancelReservationRequest {
        
        @Size(max = 500, message = "La raison ne peut pas dépasser 500 caractères")
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    // ==================== RESPONSE DTOs ====================

    /**
     * DTO pour la réponse d'une réservation
     */
    public static class ReservationResponse {
        private String id;
        private String establishmentName;
        private String type;
        private String typeDisplayName;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private Integer numberOfPersons;
        private BigDecimal price;
        private String location;
        private String status;
        private String statusDisplayName;
        private String notes;
        private String confirmationCode;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime cancelledAt;
        private String cancellationReason;

        // Constructeur vide
        public ReservationResponse() {}

        // Getters et Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEstablishmentName() {
            return establishmentName;
        }

        public void setEstablishmentName(String establishmentName) {
            this.establishmentName = establishmentName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTypeDisplayName() {
            return typeDisplayName;
        }

        public void setTypeDisplayName(String typeDisplayName) {
            this.typeDisplayName = typeDisplayName;
        }

        public LocalDate getReservationDate() {
            return reservationDate;
        }

        public void setReservationDate(LocalDate reservationDate) {
            this.reservationDate = reservationDate;
        }

        public LocalTime getReservationTime() {
            return reservationTime;
        }

        public void setReservationTime(LocalTime reservationTime) {
            this.reservationTime = reservationTime;
        }

        public Integer getNumberOfPersons() {
            return numberOfPersons;
        }

        public void setNumberOfPersons(Integer numberOfPersons) {
            this.numberOfPersons = numberOfPersons;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusDisplayName() {
            return statusDisplayName;
        }

        public void setStatusDisplayName(String statusDisplayName) {
            this.statusDisplayName = statusDisplayName;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getConfirmationCode() {
            return confirmationCode;
        }

        public void setConfirmationCode(String confirmationCode) {
            this.confirmationCode = confirmationCode;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public LocalDateTime getCancelledAt() {
            return cancelledAt;
        }

        public void setCancelledAt(LocalDateTime cancelledAt) {
            this.cancelledAt = cancelledAt;
        }

        public String getCancellationReason() {
            return cancellationReason;
        }

        public void setCancellationReason(String cancellationReason) {
            this.cancellationReason = cancellationReason;
        }
    }

    /**
     * DTO pour les statistiques des réservations
     */
    public static class ReservationStatsResponse {
        private long totalReservations;
        private long pendingReservations;
        private long confirmedReservations;
        private long cancelledReservations;
        private long completedReservations;
        private long upcomingReservations;

        // Getters et Setters
        public long getTotalReservations() {
            return totalReservations;
        }

        public void setTotalReservations(long totalReservations) {
            this.totalReservations = totalReservations;
        }

        public long getPendingReservations() {
            return pendingReservations;
        }

        public void setPendingReservations(long pendingReservations) {
            this.pendingReservations = pendingReservations;
        }

        public long getConfirmedReservations() {
            return confirmedReservations;
        }

        public void setConfirmedReservations(long confirmedReservations) {
            this.confirmedReservations = confirmedReservations;
        }

        public long getCancelledReservations() {
            return cancelledReservations;
        }

        public void setCancelledReservations(long cancelledReservations) {
            this.cancelledReservations = cancelledReservations;
        }

        public long getCompletedReservations() {
            return completedReservations;
        }

        public void setCompletedReservations(long completedReservations) {
            this.completedReservations = completedReservations;
        }

        public long getUpcomingReservations() {
            return upcomingReservations;
        }

        public void setUpcomingReservations(long upcomingReservations) {
            this.upcomingReservations = upcomingReservations;
        }
    }
}
