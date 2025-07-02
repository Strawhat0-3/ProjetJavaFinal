package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Chauffeur;
import org.hibernate.Session;
import java.util.List;

import static org.example.projetjavafinal.util.HibernateUtil.getSessionFactory;

public class ChauffeurDAO extends GenericDAOImpl<Chauffeur> {
    
    public List<Chauffeur> findByDisponible(boolean disponible) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Chauffeur c WHERE c.disponible = :disponible", 
                Chauffeur.class)
                .setParameter("disponible", disponible)
                .list();
        }
    }
    
    public void updateDisponibilite(Long chauffeurId, boolean disponible) {
        try (Session session = getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            Chauffeur chauffeur = session.get(Chauffeur.class, chauffeurId);
            if (chauffeur != null) {
                chauffeur.setDisponible(disponible);
                session.merge(chauffeur);
            }
            transaction.commit();
        }
    }
}