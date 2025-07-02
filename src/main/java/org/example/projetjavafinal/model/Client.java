package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "points_fidelite")
    private Integer pointsFidelite = 0;

    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}