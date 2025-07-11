package org.example.projetjavafinal.exception;

// Exceptions liées à l'authentification
public class AuthentificationException extends LocationExceptionHandler {
    public AuthentificationException(String message) {
        super(message);
    }

    @Override
    public String getTitre() {
        return "";
    }
}
