package com.akabli.booking.service;

import com.akabli.booking.dto.BookingRequest;
import com.akabli.booking.dto.BookingResponse;
import com.akabli.booking.entity.*;
import com.akabli.booking.exception.BusinessException;
import com.akabli.booking.repository.BookingRepository;
import com.akabli.booking.repository.OfferRepository;
import com.akabli.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private AppUser client;
    private Offer offer;

    @BeforeEach
    void setUp() {
        client = new AppUser("Client Test", "client@test.ma", "hash", Role.CLIENT);
        offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Omra test");
        offer.setType(OfferType.OMRA);
        offer.setDestination("La Mecque");
        offer.setPrice(new BigDecimal("10000"));
        offer.setDepartureDate(LocalDate.now().plusMonths(1));
        offer.setReturnDate(LocalDate.now().plusMonths(1).plusDays(15));
        offer.setTotalSeats(5);
    }

    @Test
    void create_reservesSeatsAndComputesTotalPrice() {
        when(userRepository.findByEmail("client@test.ma")).thenReturn(Optional.of(client));
        when(offerRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(offer));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.create("client@test.ma", new BookingRequest(1L, 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("20000");
        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(offer.getAvailableSeats()).isEqualTo(3);
    }

    @Test
    void create_failsWhenNotEnoughSeats() {
        when(userRepository.findByEmail("client@test.ma")).thenReturn(Optional.of(client));
        when(offerRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(offer));

        assertThatThrownBy(() -> bookingService.create("client@test.ma", new BookingRequest(1L, 6)))
                .isInstanceOf(BusinessException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_releasesSeats() {
        offer.reserveSeats(2);
        Booking booking = booking(BookingStatus.PENDING);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(offerRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(offer));

        BookingResponse response = bookingService.cancel(10L, "client@test.ma", false);

        assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(offer.getAvailableSeats()).isEqualTo(5);
    }

    @Test
    void cancel_byAnotherClient_isForbidden() {
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking(BookingStatus.PENDING)));

        assertThatThrownBy(() -> bookingService.cancel(10L, "autre@test.ma", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void confirm_onlyWorksForPendingBooking() {
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking(BookingStatus.CANCELLED)));

        assertThatThrownBy(() -> bookingService.confirm(10L))
                .isInstanceOf(BusinessException.class);
    }

    private Booking booking(BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setOffer(offer);
        booking.setUser(client);
        booking.setSeats(2);
        booking.setTotalPrice(new BigDecimal("20000"));
        booking.setStatus(status);
        return booking;
    }
}
