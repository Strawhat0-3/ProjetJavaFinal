package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import org.example.projetjavafinal.Observer;
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
        this.vehiculeService = new VehiculeService();
    }

    @FXML
    public void initialize() {
        categorieCombo.setItems(FXCollections.observableArrayList(Vehicule.Categorie.values()));
        chargerVehicules();
    }

    @FXML
    private void handleAjouterVehicule() {
        try {
            Vehicule vehicule = new Vehicule();
            vehicule.setMarque(marqueField.getText());
            vehicule.setModele(modeleField.getText());
            vehicule.setTarifJournalier(Double.parseDouble(tarifField.getText()));
            vehicule.setImmatriculation(immatriculationField.getText());
            vehicule.setCategorie(categorieCombo.getValue());
            vehicule.setDisponible(true);

            vehiculeService.ajouterVehicule(vehicule);
            chargerVehicules();
            viderChamps();
        } catch (Exception e) {
            // Gérer l'erreur
        }
    }

    private void chargerVehicules() {
        vehiculeTable.setItems(
            FXCollections.observableArrayList(
                vehiculeService.trouverTousLesVehicules()
            )
        );
    }

    private void viderChamps() {
        marqueField.clear();
        modeleField.clear();
        tarifField.clear();
        immatriculationField.clear();
        categorieCombo.setValue(null);
    }

    @Override
    public void update() {

    }
}