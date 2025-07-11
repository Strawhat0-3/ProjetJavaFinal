package org.example.projetjavafinal.exception;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

public class ExceptionHandler {
    public static void handleException(LocationExceptionHandler ex, Alert.AlertType type) {
        //log.error("Erreur détectée: " + ex.getTitre(), ex); // Log the title and exception
        afficherAlerte(type, ex.getTitre(), ex.getMessage());
    }

    // Overload for generic exceptions (if needed)
    public static void handleException(Exception ex, Alert.AlertType type) {
        //log.error("Erreur détectée", ex);
        afficherAlerte(type, "Erreur", ex.getMessage());
    }

    private static void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}