package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "factures")
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Double montantHT;

    @Column(nullable = false)
    private Double montantTTC;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE;

    public enum StatutPaiement {
        EN_ATTENTE,
        PAYE,
        ANNULE
    }
}