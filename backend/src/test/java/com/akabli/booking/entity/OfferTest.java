package com.akabli.booking.entity;

import com.akabli.booking.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest {

    private Offer offerWithSeats(int total) {
        Offer offer = new Offer();
        offer.setTotalSeats(total);
        return offer;
    }

    @Test
    void reserveSeats_decreasesAvailableSeats() {
        Offer offer = offerWithSeats(10);

        offer.reserveSeats(3);

        assertThat(offer.getAvailableSeats()).isEqualTo(7);
    }

    @Test
    void reserveSeats_throwsWhenNotEnoughSeats() {
        Offer offer = offerWithSeats(2);

        assertThatThrownBy(() -> offer.reserveSeats(3))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Places insuffisantes");
    }

    @Test
    void releaseSeats_neverGoesBelowZero() {
        Offer offer = offerWithSeats(5);
        offer.reserveSeats(1);

        offer.releaseSeats(4);

        assertThat(offer.getBookedSeats()).isZero();
    }
}
