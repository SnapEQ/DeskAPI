package com.team1.deskapi.desk.dtos.floor;

import com.team1.deskapi.desk.domain.enums.FloorStatus;

import java.util.UUID;

public record FloorResponse(
        UUID id,
        String number,
        FloorStatus status
) {
}
