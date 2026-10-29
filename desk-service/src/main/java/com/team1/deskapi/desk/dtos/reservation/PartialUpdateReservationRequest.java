package com.team1.deskapi.desk.dtos.reservation;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record PartialUpdateReservationRequest(

        UUID deskId,

        @FutureOrPresent(message = "Reservation date must be today or later")
        LocalDate reservationDate
) {
}
