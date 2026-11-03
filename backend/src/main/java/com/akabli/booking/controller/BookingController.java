package com.akabli.booking.controller;

import com.akabli.booking.dto.BookingRequest;
import com.akabli.booking.dto.BookingResponse;
import com.akabli.booking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Réservations du client connecté. */
@RestController
@RequestMapping("/api/bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest request, Authentication auth) {
        return bookingService.create(auth.getName(), request);
    }

    @GetMapping("/me")
    public List<BookingResponse> mine(Authentication auth) {
        return bookingService.findMine(auth.getName());
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, Authentication auth) {
        return bookingService.cancel(id, auth.getName(), false);
    }
}
