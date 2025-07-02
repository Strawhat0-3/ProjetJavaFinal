package org.example.projetjavafinal.service;

import org.example.projetjavafinal.dao.ChauffeurDAO;
import org.example.projetjavafinal.model.Chauffeur;

import java.util.List;
import java.util.Optional;

public class ChauffeurService {
    private final ChauffeurDAO chauffeurDAO;

    public ChauffeurService() {
        this.chauffeurDAO = new ChauffeurDAO();
    }

    public Chauffeur ajouterChauffeur(Chauffeur chauffeur) {
        return chauffeurDAO.save(chauffeur);
    }

    public List<Chauffeur> trouverChauffeursDisponibles() {
        return chauffeurDAO.findByDisponible(true);
    }

    public Optional<Chauffeur> trouverChauffeurParId(Long id) {
        return chauffeurDAO.findById(id);
    }

    public void mettreAJourDisponibilite(Long chauffeurId, boolean disponible) {
        chauffeurDAO.updateDisponibilite(chauffeurId, disponible);
    }

    public List<Chauffeur> trouverTousChauffeurs() {
        return chauffeurDAO.findAll();
    }

    public void supprimerChauffeur(Long id) {
        chauffeurDAO.deleteById(id);
    }

    public void mettreAJourChauffeur(Chauffeur chauffeur) {
        chauffeurDAO.update(chauffeur);
    }
}