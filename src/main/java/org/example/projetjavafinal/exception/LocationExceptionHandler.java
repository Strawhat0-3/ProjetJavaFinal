package org.example.projetjavafinal.exception;

// Exception de base pour l'application
public abstract class LocationExceptionHandler extends RuntimeException {
    public LocationExceptionHandler(String message) {
        super(message);
    }

    public LocationExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }

    // Méthode abstraite pour obtenir le titre de l'erreur
    public abstract String getTitre();
}

