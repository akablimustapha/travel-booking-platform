package com.akabli.booking.service;

import com.akabli.booking.dto.OfferRequest;
import com.akabli.booking.dto.OfferResponse;
import com.akabli.booking.entity.Offer;
import com.akabli.booking.entity.OfferType;
import com.akabli.booking.exception.BusinessException;
import com.akabli.booking.exception.NotFoundException;
import com.akabli.booking.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> findActive(OfferType type) {
        List<Offer> offers = (type == null)
                ? offerRepository.findByActiveTrueOrderByDepartureDateAsc()
                : offerRepository.findByActiveTrueAndTypeOrderByDepartureDateAsc(type);
        return offers.stream().map(OfferResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OfferResponse findById(Long id) {
        return OfferResponse.from(getActiveOffer(id));
    }

    @Transactional
    public OfferResponse create(OfferRequest request) {
        validateDates(request);
        Offer offer = new Offer();
        apply(offer, request);
        return OfferResponse.from(offerRepository.save(offer));
    }

    @Transactional
    public OfferResponse update(Long id, OfferRequest request) {
        validateDates(request);
        Offer offer = getActiveOffer(id);
        if (request.totalSeats() < offer.getBookedSeats()) {
            throw new BusinessException("Impossible : " + offer.getBookedSeats() + " place(s) déjà réservée(s)");
        }
        apply(offer, request);
        return OfferResponse.from(offer);
    }

    /** Suppression logique : l'offre n'est plus visible mais l'historique des réservations reste. */
    @Transactional
    public void deactivate(Long id) {
        getActiveOffer(id).setActive(false);
    }

    private Offer getActiveOffer(Long id) {
        return offerRepository.findById(id)
                .filter(Offer::isActive)
                .orElseThrow(() -> new NotFoundException("Offre introuvable : " + id));
    }

    private void validateDates(OfferRequest request) {
        if (!request.returnDate().isAfter(request.departureDate())) {
            throw new BusinessException("La date de retour doit être après la date de départ");
        }
    }

    private void apply(Offer offer, OfferRequest r) {
        offer.setTitle(r.title());
        offer.setDescription(r.description());
        offer.setType(r.type());
        offer.setDestination(r.destination());
        offer.setPrice(r.price());
        offer.setDepartureDate(r.departureDate());
        offer.setReturnDate(r.returnDate());
        offer.setTotalSeats(r.totalSeats());
    }
}
