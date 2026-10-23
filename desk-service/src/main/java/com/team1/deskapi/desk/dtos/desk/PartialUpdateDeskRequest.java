package com.team1.deskapi.desk.dtos.desk;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PartialUpdateDeskRequest(

        @Size(min = 1, max = 3, message = "Number must be between 1 and 3 characters")
        String number,

        @Size(max = 255, message = "Description must be between 0 and 255 characters")
        String description,

        UUID floorId
) {
}
