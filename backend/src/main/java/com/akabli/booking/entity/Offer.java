package com.akabli.booking.entity;

import com.akabli.booking.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferType type;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int bookedSeats;

    @Column(nullable = false)
    private boolean active = true;

    public int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    /** Réserve des places. Lève une exception s'il n'y a pas assez de places. */
    public void reserveSeats(int seats) {
        if (seats <= 0) {
            throw new BusinessException("Le nombre de places doit être positif");
        }
        if (seats > getAvailableSeats()) {
            throw new BusinessException("Places insuffisantes : il reste " + getAvailableSeats() + " place(s)");
        }
        bookedSeats += seats;
    }

    /** Libère des places (annulation d'une réservation). */
    public void releaseSeats(int seats) {
        bookedSeats = Math.max(0, bookedSeats - seats);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OfferType getType() { return type; }
    public void setType(OfferType type) { this.type = type; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
