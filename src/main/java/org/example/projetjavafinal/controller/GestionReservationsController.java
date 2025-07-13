package org.example.projetjavafinal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.*;
import org.example.projetjavafinal.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class GestionReservationsController implements Observer {
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
        this.vehiculeService = new VehiculeService(new VehiculeDAO());
        this.chauffeurService = new ChauffeurService();
    }

    @FXML
    public void initialize() {
        reservationService.addObserver(this);
        setupTableColumns();
        setupSpinners();
        chargerDonnees();
    }

//    private void setupTableColumns() {
//        TableColumn<Reservation, String> clientCol = new TableColumn<>("Client");
//        clientCol.setCellValueFactory(data ->
//            new SimpleStringProperty(data.getValue().getClient().getNom()));
//
//        TableColumn<Reservation, String> vehiculeCol = new TableColumn<>("Véhicule");
//        vehiculeCol.setCellValueFactory(data ->
//            new SimpleStringProperty(data.getValue().getVehicule().getMarque() + " " +
//                                   data.getValue().getVehicule().getModele()));
//
//        TableColumn<Reservation, LocalDateTime> dateDebutCol = new TableColumn<>("Date début");
//        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
//
//        TableColumn<Reservation, LocalDateTime> dateFinCol = new TableColumn<>("Date fin");
//        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
//
//        TableColumn<Reservation, String> statutCol = new TableColumn<>("Statut");
//        statutCol.setCellValueFactory(data ->
//            new SimpleStringProperty(data.getValue().getStatut().toString()));
//
//        reservationTable.getColumns().addAll(
//            clientCol, vehiculeCol, dateDebutCol, dateFinCol, statutCol
//        );
//    }

//    private void setupSpinners() {
//        SpinnerValueFactory<Integer> heureDebutFactory =
//            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9);
//        SpinnerValueFactory<Integer> heureFinFactory =
//            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 18);
//
//        heureDebutSpinner.setValueFactory(heureDebutFactory);
//        heureFinSpinner.setValueFactory(heureFinFactory);
//    }

//    private void chargerDonnees() {
//        // Chargement des clients
//        clientCombo.setItems(FXCollections.observableArrayList(clientService.trouverTousLesClients()));
//
//        // Chargement des véhicules disponibles
//        vehiculeCombo.setItems(FXCollections.observableArrayList(vehiculeService.trouverVehiculesDisponibles()));
//
//        // Chargement des chauffeurs disponibles
//        chauffeurCombo.setItems(FXCollections.observableArrayList(chauffeurService.trouverChauffeursDisponibles()));
//
//        // Chargement des réservations actives
//        reservationTable.setItems(FXCollections.observableArrayList(reservationService.trouverReservationsActives()));
//    }

    private Reservation creerReservationDepuisFormulaire() {
        Client client = clientCombo.getValue();
        Vehicule vehicule = vehiculeCombo.getValue();
        Chauffeur chauffeur = chauffeurCombo.getValue();
        
        if (client == null || vehicule == null || dateDebutPicker.getValue() == null || 
            dateFinPicker.getValue() == null) {
            afficherErreur("Erreur de saisie", "Veuillez remplir tous les champs obligatoires");
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
      //  reservation.setChauffeur(chauffeur);
        
        LocalDateTime dateDebut = dateDebutPicker.getValue().atTime(heureDebutSpinner.getValue(), 0);
        LocalDateTime dateFin = dateFinPicker.getValue().atTime(heureFinSpinner.getValue(), 0);
        
        if (dateDebut.isAfter(dateFin)) {
            afficherErreur("Erreur de saisie", "La date de début doit être antérieure à la date de fin");
            return null;
        }

        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatut(Reservation.StatutReservation.EN_ATTENTE);

        return reservation;
    }

    @Override
    public void update(String type, Object data) {
        if ("RESERVATION_UPDATED".equals(type)) {
            @SuppressWarnings("unchecked")
            List<Reservation> reservations = (List<Reservation>) data;
            reservationTable.setItems(FXCollections.observableArrayList(reservations));
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCreerReservation() {
        Reservation reservation = creerReservationDepuisFormulaire();
        if (reservation != null) {
            reservationService.creerReservation(reservation);
            viderFormulaire();
        }
    }

    private void viderFormulaire() {
        clientCombo.setValue(null);
        vehiculeCombo.setValue(null);
        chauffeurCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        heureDebutSpinner.getValueFactory().setValue(9);
        heureFinSpinner.getValueFactory().setValue(18);
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
        TableColumn<Reservation, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getClient().getNom()));

        TableColumn<Reservation, String> vehiculeCol = new TableColumn<>("Véhicule");
        vehiculeCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getVehicule().getMarque() + " " +
                                data.getValue().getVehicule().getModele()
                ));

        reservationTable.getColumns().addAll(clientCol, vehiculeCol);
    }

    private void chargerDonnees() {
        List<Client> clients = clientService.trouverTousLesClients();
        clientCombo.setItems(FXCollections.observableArrayList(clients));

        List<Vehicule> vehicules = vehiculeService.trouverVehiculesDisponibles();
        vehiculeCombo.setItems(FXCollections.observableArrayList(vehicules));

        List<Chauffeur> chauffeurs = chauffeurService.trouverChauffeursDisponibles();
        chauffeurCombo.setItems(FXCollections.observableArrayList(chauffeurs));

        List<Reservation> reservations = reservationService.trouverReservationsActives();
        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void handleAnnulerReservation(ActionEvent actionEvent) {
        // Get the selected reservation from the table
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une réservation à annuler");
            return;
        }

        // Confirm cancellation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer l'annulation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Update reservation status to cancelled
                    selectedReservation.setStatut(Reservation.StatutReservation.ANNULEE);
                   // reservationService.mettreAJourReservation(selectedReservation);

                    // Show success message
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Réservation annulée");
                    success.setHeaderText(null);
                    success.setContentText("La réservation a été annulée avec succès");
                    success.showAndWait();

                } catch (Exception e) {
                    afficherErreur("Erreur", "Impossible d'annuler la réservation : " + e.getMessage());
                }
            }
        });
    }
}