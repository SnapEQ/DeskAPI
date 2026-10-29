package com.team1.deskapi.desk.dtos.reservation;

import com.team1.deskapi.desk.domain.enums.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        ReservationStatus status,
        UUID deskId,
        UUID userId,
        LocalDate reservationDate,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt
) {
}
