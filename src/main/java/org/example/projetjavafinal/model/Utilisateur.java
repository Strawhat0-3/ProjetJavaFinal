package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String motDePasse;

    public enum Role {
        ADMIN,
        EMPLOYE,
        CLIENT
    }
}