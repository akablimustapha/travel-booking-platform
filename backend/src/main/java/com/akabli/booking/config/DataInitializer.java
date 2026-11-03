package com.akabli.booking.config;

import com.akabli.booking.entity.AppUser;
import com.akabli.booking.entity.Offer;
import com.akabli.booking.entity.OfferType;
import com.akabli.booking.entity.Role;
import com.akabli.booking.repository.OfferRepository;
import com.akabli.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Données de démonstration, créées seulement si la base est vide. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           OfferRepository offerRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new AppUser("Admin Démo", "admin@demo.ma", passwordEncoder.encode("Admin123!"), Role.ADMIN));
            userRepository.save(new AppUser("Client Démo", "client@demo.ma", passwordEncoder.encode("Client123!"), Role.CLIENT));
        }
        if (offerRepository.count() == 0) {
            LocalDate today = LocalDate.now();
            offerRepository.save(offer("Omra économique – 15 jours", OfferType.OMRA, "La Mecque & Médine",
                    "Hôtel 3* à 800 m du Haram, vols aller-retour, visa et transferts inclus.",
                    "18500", today.plusMonths(1), 15, 30));
            offerRepository.save(offer("Omra confort – Ramadan", OfferType.OMRA, "La Mecque & Médine",
                    "Hôtel 5* face au Haram pendant les 10 derniers jours du Ramadan.",
                    "32000", today.plusMonths(3), 12, 20));
            offerRepository.save(offer("Hajj 2027 – formule complète", OfferType.HAJJ, "La Mecque, Mina, Arafat",
                    "Encadrement religieux, tentes climatisées à Mina, pension complète.",
                    "78000", today.plusMonths(7), 21, 15));
            offerRepository.save(offer("Circuit Istanbul & Cappadoce", OfferType.VOYAGE, "Turquie",
                    "8 jours, hôtels 4*, vol en montgolfière en option.",
                    "9900", today.plusMonths(2), 8, 25));
        }
    }

    private Offer offer(String title, OfferType type, String destination, String description,
                        String price, LocalDate departure, int days, int seats) {
        Offer o = new Offer();
        o.setTitle(title);
        o.setType(type);
        o.setDestination(destination);
        o.setDescription(description);
        o.setPrice(new BigDecimal(price));
        o.setDepartureDate(departure);
        o.setReturnDate(departure.plusDays(days));
        o.setTotalSeats(seats);
        return o;
    }
}
