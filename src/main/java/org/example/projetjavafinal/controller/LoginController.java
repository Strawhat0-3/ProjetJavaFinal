package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.projetjavafinal.service.AuthentificationService;
import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.service.ClientService;

public class LoginController {
    private MainController mainController;  // Ajout de cette ligne

    @FXML
    private TextField loginField;
    
    @FXML
    private PasswordField passwordField;
    
    private final AuthentificationService authService;
    private final ClientService clientService;
    
    public LoginController() {
        this.authService = new AuthentificationService();
        this.clientService = new ClientService();
    }
    
    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();
        
        try {
            Utilisateur utilisateur = authService.authentifier(login, password);
            if (utilisateur != null) {
                switch (utilisateur.getRole()) {
                    case ADMIN -> mainController.afficherGestionVehicules(); // Modification ici
                    case EMPLOYE -> ouvrirDashboardEmploye();
                    case CLIENT -> ouvrirDashboardClient(utilisateur);
                }
            }
        } catch (Exception e) {
            afficherErreur("Échec de connexion", "Login ou mot de passe incorrect");
        }
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    private void ouvrirDashboardAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/vue/GestionVehiculeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Dashboard Administrateur");
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le dashboard administrateur");
        }
    }
    
    private void ouvrirDashboardEmploye() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/vue/GestionReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Dashboard Employé");
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le dashboard employé");
        }
    }
    
    private void ouvrirDashboardClient(Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/vue/TableauDeBordClientView.fxml"));
            Parent root = loader.load();
            
            DashboardClientController controller = loader.getController();
            clientService.trouverClientParId(utilisateur.getId())
                .ifPresent(controller::setClientConnecte);
            
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Dashboard Client");
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le dashboard client");
        }
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}