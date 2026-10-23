package com.team1.deskapi.desk.dtos.desk;

import com.team1.deskapi.desk.domain.enums.DeskStatus;

import java.util.UUID;

public record DeskResponse(
        UUID id,
        String number,
        String description,
        UUID floorId,
        DeskStatus status
) {
}
