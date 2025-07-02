package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.example.projetjavafinal.service.AuthentificationService;
import org.example.projetjavafinal.model.Utilisateur;

public class LoginController {
    @FXML
    private TextField loginField;
    
    @FXML
    private PasswordField passwordField;
    
    private final AuthentificationService authService;
    
    public LoginController() {
        this.authService = new AuthentificationService();
    }
    
    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();
        
        try {
            Utilisateur utilisateur = authService.authentifier(login, password);
            if (utilisateur != null) {
                // Rediriger vers le dashboard approprié selon le rôle
                switch (utilisateur.getRole()) {
                    case ADMIN -> ouvrirDashboardAdmin();
                    case EMPLOYE -> ouvrirDashboardEmploye();
                    case CLIENT -> ouvrirDashboardClient(utilisateur);
                }
            }
        } catch (Exception e) {
            afficherErreur("Échec de connexion", "Login ou mot de passe incorrect");
        }
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}