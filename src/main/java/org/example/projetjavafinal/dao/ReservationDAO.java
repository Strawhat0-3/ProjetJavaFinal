package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.model.Reservation;
import org.hibernate.Session;

import java.util.Arrays;
import java.util.List;

public class ReservationDAO extends GenericDAOImpl<Reservation> {
    public List<Reservation> findActiveReservations() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Reservation r WHERE r.statut IN (:statuts)", 
                Reservation.class)
                .setParameter("statuts", Arrays.asList(
                    Reservation.StatutReservation.EN_ATTENTE,
                    Reservation.StatutReservation.CONFIRMEE,
                    Reservation.StatutReservation.EN_COURS
                ))
                .list();
        }
    }
    
    public List<Reservation> findByClient(Client client) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Reservation r WHERE r.client = :client", 
                Reservation.class)
                .setParameter("client", client)
                .list();
        }
    }
}