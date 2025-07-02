package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "services_supplementaires")
public class ServiceSupplementaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double tarif;

    @Column(nullable = false)
    private Boolean disponible = true;
}