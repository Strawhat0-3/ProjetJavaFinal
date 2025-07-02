package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Reservation;
import org.example.projetjavafinal.model.Client;
import org.hibernate.Session;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.projetjavafinal.util.HibernateUtil.getSessionFactory;

public class ReservationDAO extends GenericDAOImpl<Reservation> {
    
    public List<Reservation> findByClient(Client client) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Reservation r WHERE r.client = :client ORDER BY r.dateDebut DESC", 
                Reservation.class)
                .setParameter("client", client)
                .list();
        }
    }
    
    public List<Reservation> findActiveReservations() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Reservation r WHERE r.statut IN ('CONFIRMEE', 'EN_COURS') " +
                "AND r.dateFin >= :now ORDER BY r.dateDebut", 
                Reservation.class)
                .setParameter("now", LocalDateTime.now())
                .list();
        }
    }
    
    public List<Reservation> findByDateRange(LocalDateTime debut, LocalDateTime fin) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Reservation r WHERE " +
                "(r.dateDebut BETWEEN :debut AND :fin) OR " +
                "(r.dateFin BETWEEN :debut AND :fin) OR " +
                "(r.dateDebut <= :debut AND r.dateFin >= :fin)",
                Reservation.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .list();
        }
    }
}