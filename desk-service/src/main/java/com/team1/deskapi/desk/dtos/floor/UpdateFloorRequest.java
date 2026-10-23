package com.team1.deskapi.desk.dtos.floor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFloorRequest(
        @NotBlank(message = "Floor number is required")
        @Size(min = 1, max = 3, message = "Floor number must be between 1 and 3 characters long")
        String number
) {
}
