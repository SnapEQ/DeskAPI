package com.team1.deskapi.desk.dtos.floor;

import com.team1.deskapi.desk.domain.enums.FloorStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFloorRequest(
        @NotBlank(message = "Floor number is required")
        @Size(min = 1, max = 3, message = "Floor number must be between 1 and 3 characters long")
        String number,
        @NotNull(message = "Floor status is required")
        FloorStatus status
) {
}
