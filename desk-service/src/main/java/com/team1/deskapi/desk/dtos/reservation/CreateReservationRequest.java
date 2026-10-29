package com.team1.deskapi.desk.dtos.reservation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationRequest(

        @NotNull(message = "Desk ID is required")
        UUID deskId,

        @NotNull(message = "Reservation date is required")
        @FutureOrPresent(message = "Reservation date must be today or later")
        LocalDate reservationDate
) {
}
