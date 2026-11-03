package com.akabli.booking.controller;

import com.akabli.booking.dto.BookingResponse;
import com.akabli.booking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Suivi de toutes les réservations : réservé au rôle ADMIN. */
@RestController
@RequestMapping("/api/admin/bookings")
@SecurityRequirement(name = "bearerAuth")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> all() {
        return bookingService.findAll();
    }

    @PatchMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id) {
        return bookingService.confirm(id);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, Authentication auth) {
        return bookingService.cancel(id, auth.getName(), true);
    }
}
