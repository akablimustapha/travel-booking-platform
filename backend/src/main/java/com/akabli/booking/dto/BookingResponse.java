package com.akabli.booking.dto;

import com.akabli.booking.entity.Booking;
import com.akabli.booking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long offerId,
        String offerTitle,
        LocalDate departureDate,
        String customerName,
        String customerEmail,
        int seats,
        BigDecimal totalPrice,
        BookingStatus status,
        Instant createdAt
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getOffer().getId(), b.getOffer().getTitle(),
                b.getOffer().getDepartureDate(), b.getUser().getFullName(), b.getUser().getEmail(),
                b.getSeats(), b.getTotalPrice(), b.getStatus(), b.getCreatedAt());
    }
}
