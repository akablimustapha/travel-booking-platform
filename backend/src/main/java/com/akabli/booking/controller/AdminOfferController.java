package com.akabli.booking.controller;

import com.akabli.booking.dto.OfferRequest;
import com.akabli.booking.dto.OfferResponse;
import com.akabli.booking.service.OfferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Gestion des offres : réservé au rôle ADMIN (voir SecurityConfig). */
@RestController
@RequestMapping("/api/admin/offers")
@SecurityRequirement(name = "bearerAuth")
public class AdminOfferController {

    private final OfferService offerService;

    public AdminOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferResponse create(@Valid @RequestBody OfferRequest request) {
        return offerService.create(request);
    }

    @PutMapping("/{id}")
    public OfferResponse update(@PathVariable Long id, @Valid @RequestBody OfferRequest request) {
        return offerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        offerService.deactivate(id);
    }
}
