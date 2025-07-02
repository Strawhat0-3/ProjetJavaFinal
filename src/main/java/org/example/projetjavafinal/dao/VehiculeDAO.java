package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Vehicule;
import org.hibernate.Session;
import java.util.List;

import static org.example.projetjavafinal.util.HibernateUtil.getSessionFactory;

public class VehiculeDAO extends GenericDAOImpl<Vehicule> {
    
    public List<Vehicule> findByDisponible(boolean disponible) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Vehicule v WHERE v.disponible = :disponible", 
                Vehicule.class)
                .setParameter("disponible", disponible)
                .list();
        }
    }

    public List<Vehicule> findByCategorie(Vehicule.Categorie categorie) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Vehicule v WHERE v.categorie = :categorie", 
                Vehicule.class)
                .setParameter("categorie", categorie)
                .list();
        }
    }
}