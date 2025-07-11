package org.example.projetjavafinal.exception;

// Exceptions liées aux clients
public class ClientException extends LocationExceptionHandler {
    public ClientException(String message) {
        super(message);
    }

    @Override
    public String getTitre() {
        return "";
    }
}
