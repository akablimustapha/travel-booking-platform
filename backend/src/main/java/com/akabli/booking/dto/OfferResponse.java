package com.akabli.booking.dto;

import com.akabli.booking.entity.Offer;
import com.akabli.booking.entity.OfferType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferResponse(
        Long id,
        String title,
        String description,
        OfferType type,
        String destination,
        BigDecimal price,
        LocalDate departureDate,
        LocalDate returnDate,
        int totalSeats,
        int availableSeats
) {
    public static OfferResponse from(Offer o) {
        return new OfferResponse(o.getId(), o.getTitle(), o.getDescription(), o.getType(),
                o.getDestination(), o.getPrice(), o.getDepartureDate(), o.getReturnDate(),
                o.getTotalSeats(), o.getAvailableSeats());
    }
}
