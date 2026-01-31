package com.gogenius_api.repository.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(name = "id")
    private String id; // UUID String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String establishmentName; // Nom de l'établissement

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationType type; // Hôtel, Restaurant, Spa, Activité, Transport

    @Column(nullable = false)
    private LocalDate reservationDate; // Date de la réservation

    @Column(nullable = false)
    private LocalTime reservationTime; // Heure de la réservation

    @Column(nullable = false)
    private Integer numberOfPersons; // Nombre de personnes

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // Prix en MAD

    @Column(nullable = false)
    private String location; // Ville

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    private String notes; // Notes optionnelles

    private String confirmationCode; // Code de confirmation

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    // Enum pour le type de réservation
    public enum ReservationType {
        HOTEL("Hôtel"),
        RESTAURANT("Restaurant"),
        SPA("Spa"),
        ACTIVITY("Activité"),
        TRANSPORT("Transport");

        private final String displayName;

        ReservationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Enum pour le statut
    public enum ReservationStatus {
        PENDING("En attente"),
        CONFIRMED("Confirmée"),
        CANCELLED("Annulée"),
        COMPLETED("Terminée"),
        NO_SHOW("Non présenté");

        private final String displayName;

        ReservationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructeurs
    public Reservation() {}

    public Reservation(String id, User user, String establishmentName, ReservationType type,
                       LocalDate reservationDate, LocalTime reservationTime, Integer numberOfPersons,
                       BigDecimal price, String location) {
        this.id = id;
        this.user = user;
        this.establishmentName = establishmentName;
        this.type = type;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.numberOfPersons = numberOfPersons;
        this.price = price;
        this.location = location;
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour générer un code de confirmation
    @PrePersist
    public void generateConfirmationCode() {
        if (this.confirmationCode == null) {
            this.confirmationCode = "RES-" + System.currentTimeMillis() % 100000000;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
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
