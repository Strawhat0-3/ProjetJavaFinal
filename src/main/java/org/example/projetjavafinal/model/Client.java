package org.example.projetjavafinal.model;

import jakarta.persistence.*;
import org.example.projetjavafinal.model.Utilisateur;

// Make sure your Client entity has proper Hibernate annotations
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "points_fidelite")
    private Integer pointsFidelite;

    // ✅ IMPORTANT: This is the key relationship mapping
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "id")
    private Utilisateur utilisateur;

    // Constructors, getters, setters...
    public Client() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Integer getPointsFidelite() { return pointsFidelite; }
    public void setPointsFidelite(Integer pointsFidelite) { this.pointsFidelite = pointsFidelite; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}