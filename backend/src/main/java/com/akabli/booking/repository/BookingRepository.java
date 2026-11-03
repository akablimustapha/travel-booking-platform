package com.akabli.booking.repository;

import com.akabli.booking.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"offer", "user"})
    List<Booking> findByUserEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"offer", "user"})
    List<Booking> findAllByOrderByCreatedAtDesc();
}
