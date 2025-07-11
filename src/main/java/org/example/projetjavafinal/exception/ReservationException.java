package org.example.projetjavafinal.exception;

// Exceptions liées aux réservations
public class ReservationException extends LocationExceptionHandler {
    public ReservationException(String message) {
        super(message);
    }

    @Override
    public String getTitre() {
        return "";
    }
}
