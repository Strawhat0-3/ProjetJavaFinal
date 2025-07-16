package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.*;
import org.example.projetjavafinal.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class DashboardClientController {

    // Header
    @FXML private Label nomClientLabel;
    @FXML private Label pointsFideliteLabel;
    @FXML private Button deconnexionBtn;

    // Stats
    @FXML private Label totalReservationsLabel;
    @FXML private Label reservationsActivesLabel;
    @FXML private Label pointsFideliteStatLabel;

    // Recherche véhicules
    @FXML private ComboBox<Vehicule.Categorie> filtreCategorie;
    @FXML private DatePicker dateDebutRecherche;
    @FXML private DatePicker dateFinRecherche;
    @FXML private Label vehiculesCountLabel;
    @FXML private TableView<Vehicule> vehiculesDisponiblesTable;
    @FXML private Button reserverBtn;

    // Réservations
    @FXML private ComboBox<Reservation.StatutReservation> filtreStatutReservation;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private Button annulerBtn;
    @FXML private Button modifierBtn;

    // Profil
    @FXML private Label profilNomLabel;
    @FXML private Label profilEmailLabel;
    @FXML private Label profilTelephoneLabel;
    @FXML private Label profilPointsLabel;
    @FXML private Label statTotalReservationsLabel;
    @FXML private Label statMontantTotalLabel;
    @FXML private Label statNiveauFideliteLabel;

    // Services
    private final ClientService clientService = new ClientService();
    private final VehiculeService vehiculeService = new VehiculeService(new VehiculeDAO());
    private final ReservationService reservationService = new ReservationService();
    private Client clientConnecte;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        initialiserDonnees();
    }

    @FXML
    public void initialize() {
        setupTables();
        setupFiltres();
        setupButtons();
    }

    private void setupTables() {
        // Table réservations
        TableColumn<Reservation, String> vehiculeCol = new TableColumn<>("Véhicule");
        vehiculeCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getVehicule().getMarque() + " " + data.getValue().getVehicule().getModele()));

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDateDebut().format(dateFormatter)));

        TableColumn<Reservation, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatut().toString()));

        TableColumn<Reservation, String> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMontantTotal() + " €"));

        reservationsTable.getColumns().addAll(vehiculeCol, dateCol, statutCol, montantCol);

        // Table véhicules
        TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
        marqueCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarque()));

        TableColumn<Vehicule, String> modeleCol = new TableColumn<>("Modèle");
        modeleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModele()));

        TableColumn<Vehicule, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategorie().toString()));

        TableColumn<Vehicule, String> tarifCol = new TableColumn<>("Tarif/jour");
        tarifCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTarifJournalier() + " €"));

        vehiculesDisponiblesTable.getColumns().addAll(marqueCol, modeleCol, categorieCol, tarifCol);
    }

    private void setupFiltres() {
        filtreCategorie.setItems(FXCollections.observableArrayList(Vehicule.Categorie.values()));
        filtreStatutReservation.setItems(FXCollections.observableArrayList(Reservation.StatutReservation.values()));

        filtreCategorie.setOnAction(e -> rechercherVehicules());
        dateDebutRecherche.setOnAction(e -> rechercherVehicules());
        dateFinRecherche.setOnAction(e -> rechercherVehicules());
    }

    private void setupButtons() {
        reserverBtn.setDisable(true);
        annulerBtn.setDisable(true);
        modifierBtn.setDisable(true);

        vehiculesDisponiblesTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> reserverBtn.setDisable(newVal == null));

        reservationsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    boolean canAction = newVal != null &&
                            (newVal.getStatut() == Reservation.StatutReservation.CONFIRMEE ||
                                    newVal.getStatut() == Reservation.StatutReservation.EN_ATTENTE);
                    annulerBtn.setDisable(!canAction);
                    modifierBtn.setDisable(!canAction);
                });
    }

    private void initialiserDonnees() {
        if (clientConnecte != null) {
            nomClientLabel.setText("Bienvenue, " + clientConnecte.getNom());
            pointsFideliteLabel.setText("Points fidélité : " + clientConnecte.getPointsFidelite());

            mettreAJourStats();
            mettreAJourProfil();
            chargerReservations();
            rechercherVehicules();
        }
    }


    // Dans votre DashboardClientController, remplacez la méthode setUtilisateurConnecte par :

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        try {
            if (utilisateur == null) {
                showAlert("Erreur", "Utilisateur non fourni", Alert.AlertType.ERROR);
                return;
            }

            if (utilisateur.getRole() != Utilisateur.Role.CLIENT) {
                showAlert("Erreur", "L'utilisateur doit avoir le rôle CLIENT", Alert.AlertType.ERROR);
                return;
            }

            System.out.println("=== CONNEXION CLIENT ===");
            System.out.println("Utilisateur: " + utilisateur.getNom() + " (ID: " + utilisateur.getId() + ")");
            System.out.println("Login: " + utilisateur.getLogin());
            System.out.println("Role: " + utilisateur.getRole());

            // ✅ FIXED: First diagnostic by user ID to see what exists
            clientService.diagnostiquerClientParUtilisateur(utilisateur.getId());

            // ✅ FIXED: Find client by user relationship, not by user ID
            try {
                Client client = clientService.trouverOuCreerClient(utilisateur);

                if (client != null && client.getId() != null) {
                    this.clientConnecte = client;
                    System.out.println("✅ Client connecté avec succès: ID=" + client.getId());

                    // Now we can safely call diagnostic with the actual client ID
                    clientService.diagnostiquerClient(client.getId());

                    // Initialize the interface
                    Platform.runLater(() -> {
                        try {
                            initialiserDonnees();
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
                            showAlert("Erreur", "Erreur lors de l'initialisation de l'interface", Alert.AlertType.ERROR);
                        }
                    });

                } else {
                    throw new RuntimeException("Client créé mais ID null");
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
                e.printStackTrace();

                // Migration attempt before failing
                System.out.println("Tentative de migration...");
                clientService.migrerClientsManquants();

                // New attempt after migration
                try {
                    Client client = clientService.trouverOuCreerClient(utilisateur);
                    if (client != null) {
                        this.clientConnecte = client;
                        Platform.runLater(this::initialiserDonnees);
                    } else {
                        throw new RuntimeException("Impossible de créer le client même après migration");
                    }
                } catch (Exception e2) {
                    System.err.println("Échec même après migration: " + e2.getMessage());
                    showAlert("Erreur Critique",
                            "Impossible de charger le profil client.\nVérifiez la base de données.",
                            Alert.AlertType.ERROR);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur critique de connexion: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur Critique",
                    "Erreur lors de la connexion: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // ✅ FIXED: Update diagnostic method to work with connected client
    @FXML
    private void handleDiagnostic() {
        if (clientConnecte != null) {
            // Use the actual client ID, not the user ID
            clientService.diagnostiquerClient(clientConnecte.getId());
        } else {
            System.out.println("⚠️ Aucun client connecté pour le diagnostic");
        }
    }

    // Méthode utilitaire pour afficher les informations de debug
    private void afficherInfosDebug() {
        if (clientConnecte != null) {
            System.out.println("=== INFOS CLIENT ===");
            System.out.println("Client ID: " + clientConnecte.getId());
            System.out.println("Nom: " + clientConnecte.getNom());
            System.out.println("Email: " + clientConnecte.getEmail());
            System.out.println("Points: " + clientConnecte.getPointsFidelite());

            if (clientConnecte.getUtilisateur() != null) {
                System.out.println("Utilisateur associé: " + clientConnecte.getUtilisateur().getId());
            } else {
                System.out.println("⚠️ Aucun utilisateur associé");
            }
        }
    }


    private void mettreAJourStats() {
        List<Reservation> reservations = reservationService.trouverReservationsClient(clientConnecte);

        totalReservationsLabel.setText(String.valueOf(reservations.size()));
        pointsFideliteStatLabel.setText(String.valueOf(clientConnecte.getPointsFidelite()));

        long actives = reservations.stream()
                .filter(r -> r.getStatut() == Reservation.StatutReservation.CONFIRMEE ||
                        r.getStatut() == Reservation.StatutReservation.EN_COURS)
                .count();
        reservationsActivesLabel.setText(String.valueOf(actives));
    }

    private void mettreAJourProfil() {
        profilNomLabel.setText(clientConnecte.getNom());
        profilEmailLabel.setText(clientConnecte.getEmail());
        profilTelephoneLabel.setText(clientConnecte.getTelephone());
        profilPointsLabel.setText(clientConnecte.getPointsFidelite() + " points");

        List<Reservation> reservations = reservationService.trouverReservationsClient(clientConnecte);
        statTotalReservationsLabel.setText(String.valueOf(reservations.size()));

        BigDecimal montantTotal = reservations.stream()
                .map(Reservation::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statMontantTotalLabel.setText(String.format("%.2f €", montantTotal));

        int points = clientConnecte.getPointsFidelite();
        String niveau = points < 100 ? "Bronze" : points < 500 ? "Argent" : points < 1000 ? "Or" : "Platine";
        statNiveauFideliteLabel.setText(niveau);
    }

    private void chargerReservations() {
        List<Reservation> reservations = reservationService.trouverReservationsClient(clientConnecte);
        reservationsTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void rechercherVehicules() {
        List<Vehicule> vehicules = filtreCategorie.getValue() != null
                ? vehiculeService.trouverVehiculeParCategorie(filtreCategorie.getValue())
                : vehiculeService.trouverVehiculesDisponibles();

        if (dateDebutRecherche.getValue() != null && dateFinRecherche.getValue() != null) {
            LocalDateTime debut = LocalDateTime.of(dateDebutRecherche.getValue(), java.time.LocalTime.of(9, 0));
            LocalDateTime fin = LocalDateTime.of(dateFinRecherche.getValue(), java.time.LocalTime.of(18, 0));
            vehicules = vehicules.stream()
                    .filter(v -> vehiculeService.estDisponible(v, debut, fin))
                    .toList();
        }

        vehiculesDisponiblesTable.setItems(FXCollections.observableArrayList(vehicules));
        vehiculesCountLabel.setText(vehicules.size() + " véhicule(s) trouvé(s)");
    }

    @FXML
    private void filtrerReservations() {
        List<Reservation> reservations = reservationService.trouverReservationsClient(clientConnecte);

        if (filtreStatutReservation.getValue() != null) {
            reservations = reservations.stream()
                    .filter(r -> r.getStatut() == filtreStatutReservation.getValue())
                    .toList();
        }

        reservationsTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void rafraichirReservations() {
        chargerReservations();
        mettreAJourStats();
    }

    @FXML
    private void handleNouvelleReservation() {
        Vehicule vehicule = vehiculesDisponiblesTable.getSelectionModel().getSelectedItem();
        if (vehicule == null || dateDebutRecherche.getValue() == null || dateFinRecherche.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un véhicule et les dates", Alert.AlertType.ERROR);
            return;
        }

        if (dateDebutRecherche.getValue().isAfter(dateFinRecherche.getValue())) {
            showAlert("Erreur", "Date de début invalide", Alert.AlertType.ERROR);
            return;
        }

        try {
            Reservation reservation = new Reservation();
            reservation.setClient(clientConnecte);
            reservation.setVehicule(vehicule);
            reservation.setDateDebut(LocalDateTime.of(dateDebutRecherche.getValue(), java.time.LocalTime.of(9, 0)));
            reservation.setDateFin(LocalDateTime.of(dateFinRecherche.getValue(), java.time.LocalTime.of(18, 0)));

            reservationService.creerReservation(reservation);

            chargerReservations();
            rechercherVehicules();
            mettreAJourStats();
            mettreAJourProfil();

            showAlert("Succès", "Réservation créée avec succès", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de créer la réservation", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAnnulerReservation() {
        Reservation reservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (reservation == null) return;

        if (showConfirmation("Annuler la réservation ?")) {
            try {
                reservationService.annulerReservation(reservation.getId());
                rafraichirReservations();
                showAlert("Succès", "Réservation annulée", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'annuler", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleModifierReservation() {
        showAlert("Info", "Fonctionnalité en développement", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDeconnexion() {
        if (showConfirmation("Confirmer la déconnexion ?")) {
            try {
                // Au lieu de Platform.exit(), retourner au login
                retournerAuLogin();
            } catch (Exception e) {
                System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la déconnexion", Alert.AlertType.ERROR);
            }
        }
    }

    // 2. Ajouter cette méthode dans DashboardClientController.java
    private void retournerAuLogin() {
        try {
            // Nettoyer les données du client connecté
            clientConnecte = null;

            // Charger la vue de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/LoginView.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) deconnexionBtn.getScene().getWindow();

            // Créer une nouvelle scène avec la vue de login
            Scene loginScene = new Scene(root, 700, 600);
            stage.setTitle("Location de Voitures - Connexion");
            stage.setScene(loginScene);

            // Configurer le contrôleur de login si nécessaire
            LoginController loginController = loader.getController();
            if (loginController != null) {
                // Créer une nouvelle instance de MainController pour la navigation
                MainController mainController = new MainController();
                mainController.setPrimaryStage(stage);
                loginController.setMainController(mainController);
            }

            System.out.println("✅ Retour au login réussi");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour au login: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au login", Alert.AlertType.ERROR);
        }
    }

    // Méthodes hover (simplifiées)
    @FXML private void onDeconnexionEnter() { }
    @FXML private void onDeconnexionExit() { }
    @FXML private void onButtonEnter() { }
    @FXML private void onButtonExit() { }
    @FXML private void onReserverEnter() { }
    @FXML private void onReserverExit() { }
    @FXML private void onAnnulerEnter() { }
    @FXML private void onAnnulerExit() { }
    @FXML private void onModifierEnter() { }
    @FXML private void onModifierExit() { }

    // Utilitaires
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}