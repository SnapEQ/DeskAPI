package com.team1.deskapi.desk.dtos.desk;

import com.team1.deskapi.desk.domain.enums.DeskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateDeskRequest(
        @NotBlank(message = "Desk number is required")
        @Size(min = 1, max = 3, message = "Number must be between 1 and 3 characters")
        String number,

        @NotNull(message = "Description is required")
        @Size(max = 255, message = "Description must be between 0 and 255 characters")
        String description,

        @NotNull(message = "Floor ID is required")
        UUID floorId,

        @NotNull(message = "Desk status is required")
        DeskStatus status
) {
}
