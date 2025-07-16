package org.example.projetjavafinal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.*;
import org.example.projetjavafinal.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button annulerModificationButton;

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VehiculeService vehiculeService;
    private final ChauffeurService chauffeurService;

    private Reservation reservationEnModification = null;
    private boolean modeModification = false;

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
        setupTableSelectionListener();
        setupComboBoxCellFactories(); // AJOUTEZ CETTE LIGNE ICI
        chargerDonnees();

        // Initialement, les boutons de modification/suppression sont désactivés
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        annulerModificationButton.setVisible(false);
    }

    private void setupTableSelectionListener() {
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            modifierButton.setDisable(!hasSelection);
            supprimerButton.setDisable(!hasSelection);
        });
    }

    private Reservation creerReservationDepuisFormulaire() {
        Client client = clientCombo.getValue();
        Vehicule vehicule = vehiculeCombo.getValue();
        Chauffeur chauffeur = chauffeurCombo.getValue();

        if (client == null || vehicule == null || dateDebutPicker.getValue() == null ||
                dateFinPicker.getValue() == null) {
            afficherErreur("Erreur de saisie", "Veuillez remplir tous les champs obligatoires");
            return null;
        }

        Reservation reservation = modeModification ? reservationEnModification : new Reservation();
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setChauffeur(chauffeur);

        LocalDateTime dateDebut = dateDebutPicker.getValue().atTime(heureDebutSpinner.getValue(), 0);
        LocalDateTime dateFin = dateFinPicker.getValue().atTime(heureFinSpinner.getValue(), 0);

        if (dateDebut.isAfter(dateFin)) {
            afficherErreur("Erreur de saisie", "La date de début doit être antérieure à la date de fin");
            return null;
        }

        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);

        if (!modeModification) {
            reservation.setStatut(Reservation.StatutReservation.EN_ATTENTE);
        }

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

    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCreerReservation() {
        Reservation reservation = creerReservationDepuisFormulaire();
        if (reservation != null) {
            if (modeModification) {
                reservationService.modifierReservation(reservation);
                afficherSucces("Modification réussie", "La réservation a été modifiée avec succès");
                annulerModification();
            } else {
                reservationService.creerReservation(reservation);
                afficherSucces("Création réussie", "La réservation a été créée avec succès");
            }
            viderFormulaire();
        }
    }

    @FXML
    private void handleModifierReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une réservation à modifier");
            return;
        }

        // Vérifier que la réservation peut être modifiée
        if (selectedReservation.getStatut() == Reservation.StatutReservation.ANNULEE ||
                selectedReservation.getStatut() == Reservation.StatutReservation.TERMINEE) {
            afficherErreur("Modification impossible", "Cette réservation ne peut pas être modifiée");
            return;
        }

        // Passer en mode modification
        modeModification = true;
        reservationEnModification = selectedReservation;

        // Remplir le formulaire avec les données existantes
        remplirFormulaireAvecReservation(selectedReservation);

        // Modifier l'interface
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        annulerModificationButton.setVisible(true);

        // Changer le texte du bouton créer
        Button creerButton = (Button) reservationTable.getParent().lookup("#creerButton");
        if (creerButton != null) {
            creerButton.setText("Modifier");
        }
    }

    @FXML
    private void handleSupprimerReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une réservation à supprimer");
            return;
        }

        // Confirmation de suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer définitivement cette réservation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservationService.supprimerReservation(selectedReservation.getId());
                    afficherSucces("Suppression réussie", "La réservation a été supprimée avec succès");

                    // Réinitialiser la sélection
                    reservationTable.getSelectionModel().clearSelection();

                } catch (Exception e) {
                    afficherErreur("Erreur", "Impossible de supprimer la réservation : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleAnnulerModification() {
        annulerModification();
    }

    private void annulerModification() {
        modeModification = false;
        reservationEnModification = null;

        // Réinitialiser l'interface
        modifierButton.setDisable(reservationTable.getSelectionModel().getSelectedItem() == null);
        supprimerButton.setDisable(reservationTable.getSelectionModel().getSelectedItem() == null);
        annulerModificationButton.setVisible(false);

        // Remettre le texte du bouton créer
        Button creerButton = (Button) reservationTable.getParent().lookup("#creerButton");
        if (creerButton != null) {
            creerButton.setText("Créer");
        }

        viderFormulaire();
    }

    private void remplirFormulaireAvecReservation(Reservation reservation) {
        clientCombo.setValue(reservation.getClient());
        vehiculeCombo.setValue(reservation.getVehicule());
        chauffeurCombo.setValue(reservation.getChauffeur());

        dateDebutPicker.setValue(reservation.getDateDebut().toLocalDate());
        dateFinPicker.setValue(reservation.getDateFin().toLocalDate());

        heureDebutSpinner.getValueFactory().setValue(reservation.getDateDebut().getHour());
        heureFinSpinner.getValueFactory().setValue(reservation.getDateFin().getHour());
    }

    @FXML
    private void handleAnnulerReservation(ActionEvent actionEvent) {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une réservation à annuler");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer l'annulation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservationService.annulerReservation(selectedReservation.getId());
                    afficherSucces("Réservation annulée", "La réservation a été annulée avec succès");

                } catch (Exception e) {
                    afficherErreur("Erreur", "Impossible d'annuler la réservation : " + e.getMessage());
                }
            }
        });
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

        TableColumn<Reservation, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut().toString()));

        TableColumn<Reservation, String> dateDebutCol = new TableColumn<>("Date Début");
        dateDebutCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateDebut().toString()));

        TableColumn<Reservation, String> dateFinCol = new TableColumn<>("Date Fin");
        dateFinCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateFin().toString()));

        reservationTable.getColumns().addAll(clientCol, vehiculeCol, statutCol, dateDebutCol, dateFinCol);
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
    private void handleDeconnexion() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment vous déconnecter ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            retournerAuLogin();
        }
    }

    private void retournerAuLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) reservationTable.getScene().getWindow();
            Scene loginScene = new Scene(root, 700, 600);
            stage.setTitle("Location de Voitures - Connexion");
            stage.setScene(loginScene);

            LoginController loginController = loader.getController();
            if (loginController != null) {
                MainController mainController = new MainController();
                mainController.setPrimaryStage(stage);
                loginController.setMainController(mainController);
            }

            System.out.println("✅ Retour au login depuis la gestion des réservations");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour au login: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de retourner au login");
        }
    }

    // Ajoutez cette méthode dans votre GestionReservationsController
// N'oubliez pas d'ajouter l'import : import javafx.scene.control.ListCell;

    private void setupComboBoxCellFactories() {
        // Configuration pour le ComboBox Client
        clientCombo.setCellFactory(listView -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                } else {
                    setText(client.getNom());
                }
            }
        });

        clientCombo.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                } else {
                    setText(client.getNom());
                }
            }
        });

        // Configuration pour le ComboBox Vehicule
        vehiculeCombo.setCellFactory(listView -> new ListCell<Vehicule>() {
            @Override
            protected void updateItem(Vehicule vehicule, boolean empty) {
                super.updateItem(vehicule, empty);
                if (empty || vehicule == null) {
                    setText(null);
                } else {
                    setText(vehicule.getMarque() + " " + vehicule.getModele() +
                            " (" + vehicule.getImmatriculation() + ")");
                }
            }
        });

        vehiculeCombo.setButtonCell(new ListCell<Vehicule>() {
            @Override
            protected void updateItem(Vehicule vehicule, boolean empty) {
                super.updateItem(vehicule, empty);
                if (empty || vehicule == null) {
                    setText(null);
                } else {
                    setText(vehicule.getMarque() + " " + vehicule.getModele() +
                            " (" + vehicule.getImmatriculation() + ")");
                }
            }
        });

        // Configuration pour le ComboBox Chauffeur
        chauffeurCombo.setCellFactory(listView -> new ListCell<Chauffeur>() {
            @Override
            protected void updateItem(Chauffeur chauffeur, boolean empty) {
                super.updateItem(chauffeur, empty);
                if (empty || chauffeur == null) {
                    setText(null);
                } else {
                    setText(chauffeur.getNom() + " " + chauffeur.getPrenom());
                }
            }
        });

        chauffeurCombo.setButtonCell(new ListCell<Chauffeur>() {
            @Override
            protected void updateItem(Chauffeur chauffeur, boolean empty) {
                super.updateItem(chauffeur, empty);
                if (empty || chauffeur == null) {
                    setText(null);
                } else {
                    setText(chauffeur.getNom() + " " + chauffeur.getPrenom());
                }
            }
        });
    }
}