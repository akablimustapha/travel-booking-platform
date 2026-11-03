package com.akabli.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "L'offre est obligatoire") Long offerId,
        @Min(value = 1, message = "Au moins 1 place") @Max(value = 10, message = "10 places maximum") int seats
) {
}
