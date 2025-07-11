package org.example.projetjavafinal.exception;

// Exceptions liées aux véhicules
public class VehiculeException extends LocationExceptionHandler {
    public VehiculeException(String message) {
        super(message);
    }

    @Override
    public String getTitre() {
        return "";
    }
}
