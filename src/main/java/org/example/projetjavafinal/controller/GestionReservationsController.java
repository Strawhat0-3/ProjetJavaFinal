package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import org.example.projetjavafinal.model.*;
import org.example.projetjavafinal.service.*;
import java.time.LocalDateTime;

public class GestionReservationsController {
    @FXML
    private TableView<Reservation> reservationTable;
    
    @FXML
    private ComboBox<Client> clientCombo;
    
    @FXML
    private ComboBox<Vehicule> vehiculeCombo;
    
    @FXML
    private ComboBox<Chauffeur> chauffeurCombo;
    
    @FXML
    private DatePicker dateDebutPicker;
    
    @FXML
    private DatePicker dateFinPicker;
    
    @FXML
    private Spinner<Integer> heureDebutSpinner;
    
    @FXML
    private Spinner<Integer> heureFinSpinner;
    
    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VehiculeService vehiculeService;
    private final ChauffeurService chauffeurService;
    
    public GestionReservationsController() {
        this.reservationService = new ReservationService();
        this.clientService = new ClientService();
        this.vehiculeService = new VehiculeService();
        this.chauffeurService = new ChauffeurService();
    }
    
    @FXML
    public void initialize() {
        setupSpinners();
        chargerDonnees();
        setupTableColumns();
    }
    
    private void setupSpinners() {
        SpinnerValueFactory<Integer> heureDebutFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9);
        SpinnerValueFactory<Integer> heureFinFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 18);
            
        heureDebutSpinner.setValueFactory(heureDebutFactory);
        heureFinSpinner.setValueFactory(heureFinFactory);
    }
    
    private void setupTableColumns() {
        // Configuration des colonnes du TableView
        TableColumn<Reservation, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getClient().getNom()
            )
        );
        
        TableColumn<Reservation, String> vehiculeCol = new TableColumn<>("Véhicule");
        vehiculeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getVehicule().getMarque() + " " + 
                data.getValue().getVehicule().getModele()
            )
        );
        
        reservationTable.getColumns().addAll(clientCol, vehiculeCol);
    }
    
    private void chargerDonnees() {
        clientCombo.setItems(FXCollections.observableArrayList(clientService.trouverTousLesClients()));
        vehiculeCombo.setItems(FXCollections.observableArrayList(vehiculeService.trouverVehiculesDisponibles()));
        chauffeurCombo.setItems(FXCollections.observableArrayList(chauffeurService.trouverChauffeursDisponibles()));
        
        rafraichirTableReservations();
    }
    
    @FXML
    private void handleCreerReservation() {
        try {
            Reservation reservation = new Reservation();
            reservation.setClient(clientCombo.getValue());
            reservation.setVehicule(vehiculeCombo.getValue());
            reservation.setChauffeur(chauffeurCombo.getValue());
            
            LocalDateTime dateDebut = LocalDateTime.of(
                dateDebutPicker.getValue(),
                java.time.LocalTime.of(heureDebutSpinner.getValue(), 0)
            );
            LocalDateTime dateFin = LocalDateTime.of(
                dateFinPicker.getValue(),
                java.time.LocalTime.of(heureFinSpinner.getValue(), 0)
            );
            
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            
            reservationService.creerReservation(reservation);
            rafraichirTableReservations();
            viderChamps();
            
            afficherConfirmation("Réservation créée avec succès !");
            
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de créer la réservation: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnnulerReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                reservationService.annulerReservation(selectedReservation.getId());
                rafraichirTableReservations();
                afficherConfirmation("Réservation annulée avec succès !");
            } catch (Exception e) {
                afficherErreur("Erreur", "Impossible d'annuler la réservation: " + e.getMessage());
            }
        } else {
            afficherErreur("Erreur", "Veuillez sélectionner une réservation");
        }
    }
    
    private void rafraichirTableReservations() {
        reservationTable.setItems(
            FXCollections.observableArrayList(
                reservationService.trouverReservationsActives()
            )
        );
    }
    
    private void viderChamps() {
        clientCombo.setValue(null);
        vehiculeCombo.setValue(null);
        chauffeurCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        heureDebutSpinner.getValueFactory().setValue(9);
        heureFinSpinner.getValueFactory().setValue(18);
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