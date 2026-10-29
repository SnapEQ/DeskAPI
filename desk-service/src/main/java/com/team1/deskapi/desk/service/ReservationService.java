package com.team1.deskapi.desk.service;

import com.team1.deskapi.desk.dtos.reservation.CreateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.FullUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.PartialUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.ReservationResponse;

import java.util.UUID;

public interface ReservationService {

    ReservationResponse createReservation(CreateReservationRequest request, UUID userId);

    ReservationResponse updateReservation(UUID reservationId, FullUpdateReservationRequest request);

    ReservationResponse updateReservation(UUID reservationId, PartialUpdateReservationRequest request);

    ReservationResponse cancelReservation(UUID reservationId);
}
