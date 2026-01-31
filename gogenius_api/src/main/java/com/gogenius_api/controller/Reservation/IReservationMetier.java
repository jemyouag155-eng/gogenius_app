package com.gogenius_api.controller.Reservation;

import com.gogenius_api.dto.ReservationDTO;
import com.gogenius_api.repository.entities.Reservation;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IReservationMetier {
    public ReservationDTO.ReservationResponse createReservation(String userId, ReservationDTO.CreateReservationRequest request);
    public List<ReservationDTO.ReservationResponse> getAllReservations(String userId);
    public Page<ReservationDTO.ReservationResponse> getReservationsPaginated(String userId, int page, int size, String sortBy, String sortDir);
    public List<ReservationDTO.ReservationResponse> getLatestReservations(String userId);
    public List<ReservationDTO.ReservationResponse> getUpcomingReservations(String userId);
    public List<ReservationDTO.ReservationResponse> getPastReservations(String userId);
    public Optional<ReservationDTO.ReservationResponse> getReservationById(String userId, String reservationId);
    public Optional<ReservationDTO.ReservationResponse> getReservationByCode(String confirmationCode);
    public ReservationDTO.ReservationResponse updateReservation(String userId, String reservationId, ReservationDTO.UpdateReservationRequest request);
    public ReservationDTO.ReservationResponse cancelReservation(String userId, String reservationId, ReservationDTO.CancelReservationRequest request);
    public ReservationDTO.ReservationResponse confirmReservation(String reservationId);
    public ReservationDTO.ReservationResponse completeReservation(String reservationId);
    public void deleteReservation(String userId, String reservationId);
    public ReservationDTO.ReservationStatsResponse getReservationStats(String userId);
    public Page<ReservationDTO.ReservationResponse> searchReservations(
            String userId,
            Reservation.ReservationStatus status,
            Reservation.ReservationType type,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size);

}
