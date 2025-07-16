package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "vehicules")
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    @Column(nullable = false)
    private BigDecimal tarifJournalier;  // Changed from Double to BigDecimal

    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categorie categorie;

    public enum Categorie {
        ECONOMIQUE,
        BERLINE,
        LUXE,
        SUV,
        UTILITAIRE
    }
}