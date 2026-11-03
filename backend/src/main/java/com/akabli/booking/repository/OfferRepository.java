package com.akabli.booking.repository;

import com.akabli.booking.entity.Offer;
import com.akabli.booking.entity.OfferType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByActiveTrueOrderByDepartureDateAsc();

    List<Offer> findByActiveTrueAndTypeOrderByDepartureDateAsc(OfferType type);

    /**
     * Verrou pessimiste : deux clients ne peuvent pas réserver
     * la dernière place en même temps.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Offer o where o.id = :id")
    Optional<Offer> findByIdForUpdate(@Param("id") Long id);
}
