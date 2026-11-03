package com.akabli.booking.controller;

import com.akabli.booking.dto.OfferResponse;
import com.akabli.booking.entity.OfferType;
import com.akabli.booking.service.OfferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints publics : consulter les offres. */
@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public List<OfferResponse> list(@RequestParam(required = false) OfferType type) {
        return offerService.findActive(type);
    }

    @GetMapping("/{id}")
    public OfferResponse get(@PathVariable Long id) {
        return offerService.findById(id);
    }
}
