package org.example.projetjavafinal.service;

import org.example.projetjavafinal.Observable;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.DAOFactory;
import org.example.projetjavafinal.dao.ReservationDAO;
import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationService implements Observable {
    private final ReservationDAO reservationDAO;
    private final List<Observer> observers = new ArrayList<>();

    public ReservationService() {
        this.reservationDAO = (ReservationDAO) DAOFactory.getDAO(Reservation.class);
    }

    public List<Reservation> trouverReservationsActives() {
        return reservationDAO.findActiveReservations();
    }

    public List<Reservation> trouverReservationsClient(Client client) {
        return reservationDAO.findByClient(client);
    }

    public Optional<Reservation> trouverReservationParId(Long id) {
        return reservationDAO.findById(id);
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String type, Object data) {
        for (Observer observer : observers) {
            observer.update(type, data);
        }
    }

    public Reservation creerReservation(Reservation reservation) {
        Reservation nouvelleReservation = reservationDAO.save(reservation);
        notifyObservers("RESERVATION_UPDATED", trouverReservationsActives());
        return nouvelleReservation;
    }

    public Reservation modifierReservation(Reservation reservation) {
        if (reservation.getId() == null) {
            throw new IllegalArgumentException("L'ID de la réservation ne peut pas être null pour une modification");
        }

        // Vérifier que la réservation existe
        Optional<Reservation> existingReservation = reservationDAO.findById(reservation.getId());
        if (!existingReservation.isPresent()) {
            throw new IllegalArgumentException("La réservation avec l'ID " + reservation.getId() + " n'existe pas");
        }

        // Vérifier que la réservation peut être modifiée
        Reservation existing = existingReservation.get();
        if (existing.getStatut() == Reservation.StatutReservation.ANNULEE ||
                existing.getStatut() == Reservation.StatutReservation.TERMINEE) {
            throw new IllegalStateException("Cette réservation ne peut pas être modifiée car elle est " + existing.getStatut());
        }

        Reservation reservationModifiee = reservationDAO.update(reservation);
        notifyObservers("RESERVATION_UPDATED", trouverReservationsActives());
        return reservationModifiee;
    }

    public void supprimerReservation(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("L'ID de la réservation ne peut pas être null");
        }

        Optional<Reservation> reservation = reservationDAO.findById(reservationId);
        if (!reservation.isPresent()) {
            throw new IllegalArgumentException("La réservation avec l'ID " + reservationId + " n'existe pas");
        }

        // Vérifier que la réservation peut être supprimée
        Reservation existing = reservation.get();
        if (existing.getStatut() == Reservation.StatutReservation.EN_COURS) {
            throw new IllegalStateException("Impossible de supprimer une réservation en cours");
        }

        reservationDAO.deleteById(reservationId);
        notifyObservers("RESERVATION_UPDATED", trouverReservationsActives());
    }

    public void annulerReservation(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("L'ID de la réservation ne peut pas être null");
        }

        Optional<Reservation> reservation = reservationDAO.findById(reservationId);
        if (!reservation.isPresent()) {
            throw new IllegalArgumentException("La réservation avec l'ID " + reservationId + " n'existe pas");
        }

        Reservation r = reservation.get();

        // Vérifier que la réservation peut être annulée
        if (r.getStatut() == Reservation.StatutReservation.ANNULEE) {
            throw new IllegalStateException("Cette réservation est déjà annulée");
        }

        if (r.getStatut() == Reservation.StatutReservation.TERMINEE) {
            throw new IllegalStateException("Impossible d'annuler une réservation terminée");
        }

        r.setStatut(Reservation.StatutReservation.ANNULEE);
        reservationDAO.update(r);
        notifyObservers("RESERVATION_UPDATED", trouverReservationsActives());
    }

    public boolean peutEtreModifiee(Reservation reservation) {
        return reservation.getStatut() != Reservation.StatutReservation.ANNULEE &&
                reservation.getStatut() != Reservation.StatutReservation.TERMINEE;
    }

    public boolean peutEtreSupprimee(Reservation reservation) {
        return reservation.getStatut() != Reservation.StatutReservation.EN_COURS;
    }

    public boolean peutEtreAnnulee(Reservation reservation) {
        return reservation.getStatut() != Reservation.StatutReservation.ANNULEE &&
                reservation.getStatut() != Reservation.StatutReservation.TERMINEE;
    }
}