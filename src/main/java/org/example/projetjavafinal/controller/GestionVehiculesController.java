package org.example.projetjavafinal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.Vehicule;
import org.example.projetjavafinal.service.VehiculeService;

import java.math.BigDecimal;
import java.util.Optional;

public class GestionVehiculesController implements Observer {
    @FXML
    private TableView<Vehicule> vehiculeTable;

    @FXML
    private TextField marqueField;

    @FXML
    private TextField modeleField;

    @FXML
    private TextField tarifField;

    @FXML
    private TextField immatriculationField;

    @FXML
    private ComboBox<Vehicule.Categorie> categorieCombo;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button annulerButton;

    private final VehiculeService vehiculeService;
    private Vehicule vehiculeEnModification = null;

    public GestionVehiculesController() {
        this.vehiculeService = new VehiculeService(new VehiculeDAO());
    }

    @FXML
    public void initialize() {
        vehiculeService.addObserver(this);
        setupTableColumns();
        chargerVehicules();
        setupCategorieCombo();
        setupTableSelection();
        mettreAJourEtatBoutons();
    }

    private void setupTableColumns() {
        TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
        marqueCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMarque()));

        TableColumn<Vehicule, String> modeleCol = new TableColumn<>("Modèle");
        modeleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getModele()));

        TableColumn<Vehicule, String> tarifCol = new TableColumn<>("Tarif (€/jour)");
        tarifCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getTarifJournalier())));

        TableColumn<Vehicule, String> immatriculationCol = new TableColumn<>("Immatriculation");
        immatriculationCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getImmatriculation()));

        TableColumn<Vehicule, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategorie().toString()));

        TableColumn<Vehicule, String> disponibleCol = new TableColumn<>("État");
        disponibleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDisponible() ? "Disponible" : "Occupé"));

        vehiculeTable.getColumns().addAll(marqueCol, modeleCol, tarifCol, immatriculationCol, categorieCol, disponibleCol);
    }

    private void setupTableSelection() {
        vehiculeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            mettreAJourEtatBoutons();
        });

        // Double-clic pour modifier
        vehiculeTable.setRowFactory(tv -> {
            TableRow<Vehicule> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleModifierVehicule();
                }
            });
            return row;
        });
    }

    private void chargerVehicules() {
        vehiculeTable.setItems(FXCollections.observableArrayList(
                vehiculeService.trouverTousLesVehicules()
        ));
    }

    private void setupCategorieCombo() {
        categorieCombo.setItems(FXCollections.observableArrayList(Vehicule.Categorie.values()));
    }

    @FXML
    private void handleAjouterVehicule() {
        if (vehiculeEnModification != null) {
            // Mode modification
            if (mettreAJourVehiculeDepuisFormulaire(vehiculeEnModification)) {
                vehiculeService.modifierVehicule(vehiculeEnModification);
                annulerModification();
            }
        } else {
            // Mode ajout
            Vehicule vehicule = creerVehiculeDepuisFormulaire();
            if (vehicule != null) {
                vehiculeService.ajouterVehicule(vehicule);
                viderChamps();
            }
        }
    }

    @FXML
    private void handleModifierVehicule() {
        Vehicule vehiculeSelectionne = vehiculeTable.getSelectionModel().getSelectedItem();
        if (vehiculeSelectionne != null) {
            vehiculeEnModification = vehiculeSelectionne;
            remplirFormulaireAvecVehicule(vehiculeSelectionne);
            mettreAJourEtatBoutons();
        }
    }

    @FXML
    private void handleSupprimerVehicule() {
        Vehicule vehiculeSelectionne = vehiculeTable.getSelectionModel().getSelectedItem();
        if (vehiculeSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le véhicule " +
                    vehiculeSelectionne.getMarque() + " " + vehiculeSelectionne.getModele() + " ?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                vehiculeService.supprimerVehicule(vehiculeSelectionne.getId());
                vehiculeTable.getSelectionModel().clearSelection();
            }
        }
    }


    private void annulerModification() {
        vehiculeEnModification = null;
        viderChamps();
        vehiculeTable.getSelectionModel().clearSelection();
        mettreAJourEtatBoutons();
    }

    private void remplirFormulaireAvecVehicule(Vehicule vehicule) {
        marqueField.setText(vehicule.getMarque());
        modeleField.setText(vehicule.getModele());
        tarifField.setText(vehicule.getTarifJournalier().toString());
        immatriculationField.setText(vehicule.getImmatriculation());
        categorieCombo.setValue(vehicule.getCategorie());
    }

    private boolean mettreAJourVehiculeDepuisFormulaire(Vehicule vehicule) {
        try {
            vehicule.setMarque(marqueField.getText());
            vehicule.setModele(modeleField.getText());
            vehicule.setTarifJournalier(BigDecimal.valueOf(Double.parseDouble(tarifField.getText())));
            vehicule.setImmatriculation(immatriculationField.getText());
            vehicule.setCategorie(categorieCombo.getValue());

            return validerVehicule(vehicule);
        } catch (NumberFormatException e) {
            afficherErreur("Erreur", "Le tarif doit être un nombre valide");
            return false;
        }
    }

    private Vehicule creerVehiculeDepuisFormulaire() {
        try {
            Vehicule vehicule = new Vehicule();
            vehicule.setMarque(marqueField.getText());
            vehicule.setModele(modeleField.getText());
            vehicule.setTarifJournalier(BigDecimal.valueOf(Double.parseDouble(tarifField.getText())));
            vehicule.setImmatriculation(immatriculationField.getText());
            vehicule.setCategorie(categorieCombo.getValue());
            vehicule.setDisponible(true);

            if (validerVehicule(vehicule)) {
                return vehicule;
            }
        } catch (NumberFormatException e) {
            afficherErreur("Erreur", "Le tarif doit être un nombre valide");
        }
        return null;
    }

    private boolean validerVehicule(Vehicule vehicule) {
        if (vehicule.getMarque() == null || vehicule.getMarque().trim().isEmpty()) {
            afficherErreur("Erreur", "La marque est obligatoire");
            return false;
        }
        if (vehicule.getModele() == null || vehicule.getModele().trim().isEmpty()) {
            afficherErreur("Erreur", "Le modèle est obligatoire");
            return false;
        }
        if (vehicule.getImmatriculation() == null || vehicule.getImmatriculation().trim().isEmpty()) {
            afficherErreur("Erreur", "L'immatriculation est obligatoire");
            return false;
        }
        if (vehicule.getCategorie() == null) {
            afficherErreur("Erreur", "La catégorie est obligatoire");
            return false;
        }
        return true;
    }

    private void mettreAJourEtatBoutons() {
        boolean vehiculeSelectionne = vehiculeTable.getSelectionModel().getSelectedItem() != null;
        boolean enModeModification = vehiculeEnModification != null;

        modifierButton.setDisable(!vehiculeSelectionne || enModeModification);
        supprimerButton.setDisable(!vehiculeSelectionne || enModeModification);

        if (enModeModification) {
            ajouterButton.setText("Confirmer modification");
        } else {
            ajouterButton.setText("Ajouter le véhicule");
        }
    }

    private void viderChamps() {
        marqueField.clear();
        modeleField.clear();
        tarifField.clear();
        immatriculationField.clear();
        categorieCombo.setValue(null);
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update(String type, Object data) {
        if ("VEHICULE_LIST_UPDATED".equals(type)) {
            chargerVehicules();
        }
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

            Stage stage = (Stage) vehiculeTable.getScene().getWindow();
            Scene loginScene = new Scene( root, 700, 600);
            stage.setTitle("Location de Voitures - Connexion");
            stage.setScene(loginScene);

            LoginController loginController = loader.getController();
            if (loginController != null) {
                MainController mainController = new MainController();
                mainController.setPrimaryStage(stage);
                loginController.setMainController(mainController);
            }

            System.out.println("✅ Retour au login depuis la gestion des véhicules");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour au login: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de retourner au login");
        }
    }

}