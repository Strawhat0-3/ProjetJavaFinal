package org.example.projetjavafinal.exception;

// Exception de base pour l'application
public abstract class LocationException extends RuntimeException {
    public LocationException(String message) {
        super(message);
    }

    public LocationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Exceptions liées aux réservations
public class ReservationException extends LocationException {
    public ReservationException(String message) {
        super(message);
    }
}

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

// Exceptions liées aux clients
public class ClientException extends LocationException {
    public ClientException(String message) {
        super(message);
    }
}

public class PointsFideliteInsuffisantsException extends ClientException {
    private final int pointsDisponibles;
    private final int pointsNecessaires;

    public PointsFideliteInsuffisantsException(int disponibles, int necessaires) {
        super(String.format("Points fidélité insuffisants : %d disponibles, %d nécessaires", 
              disponibles, necessaires));
        this.pointsDisponibles = disponibles;
        this.pointsNecessaires = necessaires;
    }
}

// Exceptions liées aux véhicules
public class VehiculeException extends LocationException {
    public VehiculeException(String message) {
        super(message);
    }
}

public class VehiculeInexistantException extends VehiculeException {
    public VehiculeInexistantException(String immatriculation) {
        super("Véhicule inexistant : " + immatriculation);
    }
}

// Exceptions liées à l'authentification
public class AuthentificationException extends LocationException {
    public AuthentificationException(String message) {
        super(message);
    }
}

// Gestionnaire d'exceptions centralisé
public class ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static void handleException(LocationException ex, Alert.AlertType type) {
        logger.error("Erreur : ", ex);
        
        String titre = "Erreur";
        String message = ex.getMessage();
        
        if (ex instanceof VehiculeIndisponibleException) {
            titre = "Véhicule Indisponible";
        } else if (ex instanceof PointsFideliteInsuffisantsException) {
            titre = "Points Fidélité Insuffisants";
        }
        
        afficherAlerte(type, titre, message);
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