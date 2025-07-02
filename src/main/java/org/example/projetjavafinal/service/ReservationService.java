package org.example.projetjavafinal.service;

import org.example.projetjavafinal.dao.ReservationDAO;
import org.example.projetjavafinal.model.Reservation;
import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.model.Vehicule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final VehiculeService vehiculeService;
    private final ClientService clientService;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.vehiculeService = new VehiculeService();
        this.clientService = new ClientService();
    }

    public Reservation creerReservation(Reservation reservation) {
        // Vérifier la disponibilité du véhicule
        if (!verifierDisponibiliteVehicule(
                reservation.getVehicule().getId(), 
                reservation.getDateDebut(), 
                reservation.getDateFin())) {
            throw new IllegalStateException("Le véhicule n'est pas disponible pour ces dates");
        }

        // Mettre à jour le statut du véhicule
        Vehicule vehicule = reservation.getVehicule();
        vehicule.setDisponible(false);
        vehiculeService.mettreAJourVehicule(vehicule);

        // Sauvegarder la réservation
        return reservationDAO.save(reservation);
    }

    public void annulerReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatut(Reservation.StatutReservation.ANNULEE);
            
            // Rendre le véhicule disponible
            Vehicule vehicule = reservation.getVehicule();
            vehicule.setDisponible(true);
            vehiculeService.mettreAJourVehicule(vehicule);
            
            reservationDAO.update(reservation);
        }
    }

    public List<Reservation> trouverReservationsClient(Client client) {
        return reservationDAO.findByClient(client);
    }

    public List<Reservation> trouverReservationsActives() {
        return reservationDAO.findActiveReservations();
    }

    private boolean verifierDisponibiliteVehicule(Long vehiculeId, LocalDateTime debut, LocalDateTime fin) {
        List<Reservation> reservationsExistantes = reservationDAO.findByDateRange(debut, fin);
        return reservationsExistantes.stream()
                .noneMatch(r -> r.getVehicule().getId().equals(vehiculeId) &&
                        r.getStatut() != Reservation.StatutReservation.ANNULEE);
    }

    public void terminerReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatut(Reservation.StatutReservation.TERMINEE);
            
            // Mettre à jour les points de fidélité du client
            Client client = reservation.getClient();
            clientService.ajouterPointsFidelite(client.getId(), calculateFidelityPoints(reservation));
            
            // Rendre le véhicule disponible
            Vehicule vehicule = reservation.getVehicule();
            vehicule.setDisponible(true);
            vehiculeService.mettreAJourVehicule(vehicule);
            
            reservationDAO.update(reservation);
        }
    }

    private Integer calculateFidelityPoints(Reservation reservation) {
        // Logique simple : 1 point par jour de location
        long nombreJours = java.time.Duration.between(
            reservation.getDateDebut(), 
            reservation.getDateFin()
        ).toDays();
        return Math.max(1, (int) nombreJours);
    }
}