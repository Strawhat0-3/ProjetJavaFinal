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

    public void annulerReservation(Long reservationId) {
        Optional<Reservation> reservation = reservationDAO.findById(reservationId);
        reservation.ifPresent(r -> {
            r.setStatut(Reservation.StatutReservation.ANNULEE);
            reservationDAO.update(r);
            notifyObservers("RESERVATION_UPDATED", trouverReservationsActives());
        });
    }
}