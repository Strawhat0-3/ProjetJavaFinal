package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id")
    private Chauffeur chauffeur;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    // Calculate the total amount based on vehicle daily rate and duration
    public BigDecimal getMontantTotal() {
        if (vehicule == null || dateDebut == null || dateFin == null) {
            return BigDecimal.ZERO;
        }

        long days = java.time.Duration.between(dateDebut, dateFin).toDays();
        if (days == 0) days = 1; // Minimum 1 day

        return vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(days));
    }

    public enum StatutReservation {
        EN_ATTENTE,
        CONFIRMEE,
        EN_COURS,
        TERMINEE,
        ANNULEE
    }
}