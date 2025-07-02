package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chauffeurs")
public class Chauffeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String numeroPermis;

    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(nullable = false)
    private Double tarifJournalier;
}