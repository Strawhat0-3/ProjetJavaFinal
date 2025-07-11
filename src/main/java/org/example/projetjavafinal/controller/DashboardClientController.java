package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.*;
import org.example.projetjavafinal.service.*;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardClientController {
    @FXML
    private Label nomClientLabel;
    
    @FXML
    private Label pointsFideliteLabel;
    
    @FXML
    private TableView<Reservation> reservationsTable;
    
    @FXML
    private TableView<Vehicule> vehiculesDisponiblesTable;
    
    @FXML
    private ComboBox<Vehicule.Categorie> filtreCategorie;
    
    @FXML
    private DatePicker dateDebutRecherche;
    
    @FXML
    private DatePicker dateFinRecherche;
    
    private final ClientService clientService;
    private final VehiculeService vehiculeService;
    private final ReservationService reservationService;
    private Client clientConnecte;
    
    public DashboardClientController() {
        this.clientService = new ClientService();
        this.vehiculeService = new VehiculeService(new VehiculeDAO());
        this.reservationService = new ReservationService();
    }
    
    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        initialiserDonnees();
    }
    
    @FXML
    public void initialize() {
        setupTables();
        setupFiltres();
    }
    
    private void setupTables() {
        // Configuration de la table des réservations
        TableColumn<Reservation, String> vehiculeCol = new TableColumn<>("Véhicule");
        vehiculeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getVehicule().getMarque() + " " + 
                data.getValue().getVehicule().getModele()
            )
        );
        
        TableColumn<Reservation, String> dateDebutCol = new TableColumn<>("Date début");
        dateDebutCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateDebut().toString()
            )
        );
        
        TableColumn<Reservation, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatut().toString()
            )
        );
        
        reservationsTable.getColumns().addAll(vehiculeCol, dateDebutCol, statutCol);
        
        // Configuration de la table des véhicules disponibles
        TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
        marqueCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getMarque()
            )
        );
        
        TableColumn<Vehicule, String> modeleCol = new TableColumn<>("Modèle");
        modeleCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getModele()
            )
        );
        
        TableColumn<Vehicule, String> tarifCol = new TableColumn<>("Tarif/jour");
        tarifCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTarifJournalier().toString() + " €"
            )
        );
        
        vehiculesDisponiblesTable.getColumns().addAll(marqueCol, modeleCol, tarifCol);
    }
    
    private void setupFiltres() {
        filtreCategorie.setItems(FXCollections.observableArrayList(Vehicule.Categorie.values()));
        filtreCategorie.getItems().add(0, null); // Option "Toutes catégories"
        
        filtreCategorie.setOnAction(e -> rechercherVehicules());
        dateDebutRecherche.setOnAction(e -> rechercherVehicules());
        dateFinRecherche.setOnAction(e -> rechercherVehicules());
    }
    
    private void initialiserDonnees() {
        if (clientConnecte != null) {
            nomClientLabel.setText("Bienvenue, " + clientConnecte.getNom());
            pointsFideliteLabel.setText("Points fidélité : " + clientConnecte.getPointsFidelite());
            
            chargerReservationsClient();
            rechercherVehicules();
        }
    }
    
    private void chargerReservationsClient() {
        List<Reservation> reservations = reservationService.trouverReservationsClient(clientConnecte);
        reservationsTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void rechercherVehicules() {
        List<Vehicule> vehicules;
        if (filtreCategorie.getValue() != null) {
            vehicules = vehiculeService.trouverVehiculeParCategorie(filtreCategorie.getValue());
        } else {
            vehicules = vehiculeService.trouverVehiculesDisponibles();
        }
        vehiculesDisponiblesTable.setItems(FXCollections.observableArrayList(vehicules));
    }
    
    @FXML
    private void handleNouvelleReservation() {
        Vehicule vehiculeSelectionne = vehiculesDisponiblesTable.getSelectionModel().getSelectedItem();
        if (vehiculeSelectionne == null) {
            afficherErreur("Erreur", "Veuillez sélectionner un véhicule");
            return;
        }
        
        if (dateDebutRecherche.getValue() == null || dateFinRecherche.getValue() == null) {
            afficherErreur("Erreur", "Veuillez sélectionner les dates de réservation");
            return;
        }
        
        try {
            Reservation nouvelleReservation = new Reservation();
            nouvelleReservation.setClient(clientConnecte);
            nouvelleReservation.setVehicule(vehiculeSelectionne);
            nouvelleReservation.setDateDebut(LocalDateTime.of(dateDebutRecherche.getValue(), java.time.LocalTime.of(9, 0)));
            nouvelleReservation.setDateFin(LocalDateTime.of(dateFinRecherche.getValue(), java.time.LocalTime.of(18, 0)));
            
            reservationService.creerReservation(nouvelleReservation);
            
            chargerReservationsClient();
            rechercherVehicules();
            afficherConfirmation("Réservation créée avec succès !");
            
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de créer la réservation : " + e.getMessage());
        }
    }
    
    private void afficherConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}