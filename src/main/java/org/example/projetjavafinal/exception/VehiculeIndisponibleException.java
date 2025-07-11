package org.example.projetjavafinal.exception;

import java.time.LocalDateTime;

public class VehiculeIndisponibleException extends ReservationException {
    private final String immatriculation;
    private final LocalDateTime debut;
    private final LocalDateTime fin;

    public VehiculeIndisponibleException(String immatriculation, LocalDateTime debut, LocalDateTime fin) {
        super(String.format("Le véhicule %s n'est pas disponible entre %s et %s", 
              immatriculation, debut, fin));
        this.immatriculation = immatriculation;
        this.debut = debut;
        this.fin = fin;
    }

    // Getters pour les détails spécifiques
    public String getImmatriculation() { return immatriculation; }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
}
