package org.example.projetjavafinal.exception;

public class VehiculeInexistantException extends VehiculeException {
    public VehiculeInexistantException(String immatriculation) {
        super("Véhicule inexistant : " + immatriculation);
    }
}
