package com.akabli.booking.dto;

import com.akabli.booking.entity.OfferType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferRequest(
        @NotBlank(message = "Le titre est obligatoire") String title,
        @Size(max = 2000) String description,
        @NotNull(message = "Le type est obligatoire") OfferType type,
        @NotBlank(message = "La destination est obligatoire") String destination,
        @NotNull @Positive(message = "Le prix doit être positif") BigDecimal price,
        @NotNull @FutureOrPresent(message = "La date de départ doit être dans le futur") LocalDate departureDate,
        @NotNull LocalDate returnDate,
        @Min(value = 1, message = "Au moins 1 place") int totalSeats
) {
}
