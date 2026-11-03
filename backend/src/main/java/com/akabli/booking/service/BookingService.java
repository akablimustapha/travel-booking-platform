package com.akabli.booking.service;

import com.akabli.booking.dto.BookingRequest;
import com.akabli.booking.dto.BookingResponse;
import com.akabli.booking.entity.AppUser;
import com.akabli.booking.entity.Booking;
import com.akabli.booking.entity.BookingStatus;
import com.akabli.booking.entity.Offer;
import com.akabli.booking.exception.BusinessException;
import com.akabli.booking.exception.NotFoundException;
import com.akabli.booking.repository.BookingRepository;
import com.akabli.booking.repository.OfferRepository;
import com.akabli.booking.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          OfferRepository offerRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse create(String userEmail, BookingRequest request) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Offer offer = offerRepository.findByIdForUpdate(request.offerId())
                .filter(Offer::isActive)
                .orElseThrow(() -> new NotFoundException("Offre introuvable : " + request.offerId()));

        offer.reserveSeats(request.seats());

        Booking booking = new Booking();
        booking.setOffer(offer);
        booking.setUser(user);
        booking.setSeats(request.seats());
        booking.setTotalPrice(offer.getPrice().multiply(BigDecimal.valueOf(request.seats())));
        booking.setStatus(BookingStatus.PENDING);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findMine(String userEmail) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream().map(BookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(BookingResponse::from).toList();
    }

    @Transactional
    public BookingResponse confirm(Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Seule une réservation en attente peut être confirmée");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return BookingResponse.from(booking);
    }

    /**
     * Annule une réservation et libère les places.
     * Un client ne peut annuler que ses propres réservations ; un admin peut tout annuler.
     */
    @Transactional
    public BookingResponse cancel(Long bookingId, String userEmail, boolean isAdmin) {
        Booking booking = getBooking(bookingId);
        if (!isAdmin && !booking.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Cette réservation ne vous appartient pas");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Cette réservation est déjà annulée");
        }
        Offer offer = offerRepository.findByIdForUpdate(booking.getOffer().getId())
                .orElseThrow(() -> new NotFoundException("Offre introuvable"));
        offer.releaseSeats(booking.getSeats());
        booking.setStatus(BookingStatus.CANCELLED);
        return BookingResponse.from(booking);
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable : " + id));
    }
}
