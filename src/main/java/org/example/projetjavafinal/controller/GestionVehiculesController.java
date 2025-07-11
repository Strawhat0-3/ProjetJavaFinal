package org.example.projetjavafinal.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.Vehicule;
import org.example.projetjavafinal.service.VehiculeService;

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
    
    private final VehiculeService vehiculeService;
    
    public GestionVehiculesController() {
        this.vehiculeService = new VehiculeService(new VehiculeDAO());
    }
    
    @FXML
    public void initialize() {
        vehiculeService.addObserver(this);
        setupTableColumns();
        chargerVehicules();
        setupCategorieCombo();
    }
    
    private void setupTableColumns() {
        TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
        marqueCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getMarque()));
            
        TableColumn<Vehicule, String> modeleCol = new TableColumn<>("Modèle");
        modeleCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getModele()));
            
        TableColumn<Vehicule, String> immatriculationCol = new TableColumn<>("Immatriculation");
        immatriculationCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getImmatriculation()));
            
        TableColumn<Vehicule, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCategorie().toString()));
            
        vehiculeTable.getColumns().addAll(marqueCol, modeleCol, immatriculationCol, categorieCol);
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
        Vehicule vehicule = creerVehiculeDepuisFormulaire();
        if (vehicule != null) {
            vehiculeService.ajouterVehicule(vehicule);
            viderChamps();
        }
    }
    
    private Vehicule creerVehiculeDepuisFormulaire() {
        try {
            Vehicule vehicule = new Vehicule();
            vehicule.setMarque(marqueField.getText());
            vehicule.setModele(modeleField.getText());
            vehicule.setTarifJournalier(Double.parseDouble(tarifField.getText()));
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
}