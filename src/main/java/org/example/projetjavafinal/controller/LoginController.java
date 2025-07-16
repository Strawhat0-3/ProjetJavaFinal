package org.example.projetjavafinal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.service.AuthentificationService;
import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.service.ClientService;

public class LoginController {
    private MainController mainController;

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
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        System.out.println("🔍 Tentative de connexion...");
        System.out.println("Login saisi: '" + login + "'");
        System.out.println("Mot de passe saisi: " + (password.isEmpty() ? "VIDE" : "****"));

        // Validation des champs
        if (login.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Champs vides détectés");
            afficherErreur("Champs manquants", "Veuillez remplir tous les champs");
            return;
        }

        try {
            System.out.println("🔍 Appel du service d'authentification...");
            Utilisateur utilisateur = authService.authentifier(login, password);

            if (utilisateur != null) {
                System.out.println("✅ Authentification réussie!");
                System.out.println("Utilisateur trouvé: " + utilisateur.getLogin());
                System.out.println("Role: " + utilisateur.getRole());
                System.out.println("ID: " + utilisateur.getId());

                switch (utilisateur.getRole()) {
                    case ADMIN -> {
                        System.out.println("🔧 Redirection vers le dashboard administrateur");
                        if (mainController != null) {
                            mainController.afficherGestionVehicules();
                        } else {
                            System.err.println("❌ MainController est null!");
                            afficherErreur("Erreur", "Erreur interne de navigation");
                        }
                    }
                    case EMPLOYE -> {
                        System.out.println("👥 Redirection vers le dashboard employé");
                        ouvrirDashboardEmploye();
                    }
                    case CLIENT -> {
                        System.out.println("🏠 Redirection vers le dashboard client");
                        ouvrirDashboardClient(utilisateur);
                    }
                    default -> {
                        System.err.println("❌ Role non reconnu: " + utilisateur.getRole());
                        afficherErreur("Erreur", "Rôle utilisateur non reconnu");
                    }
                }
            } else {
                System.out.println("❌ Authentification échouée - utilisateur null");
                afficherErreur("Échec de connexion", "Login ou mot de passe incorrect");
            }
        } catch (Exception e) {
            System.err.println("❌ Exception lors de l'authentification:");
            System.err.println("Type: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur de connexion", "Erreur lors de l'authentification: " + e.getMessage());
        }
    }

    // Gestionnaire d'événements manquant - ajouté pour résoudre l'erreur FXML
    @FXML
    private void onFieldEnter(MouseEvent event) {
        System.out.println("🖱️ Souris entrée dans le champ");
        // Vous pouvez ajouter ici du code pour changer l'apparence du champ
        // Par exemple, changer la couleur de fond ou ajouter un effet visuel
    }

    // Gestionnaire optionnel pour quand la souris quitte le champ
    @FXML
    private void onFieldExit(MouseEvent event) {
        System.out.println("🖱️ Souris sortie du champ");
        // Code pour restaurer l'apparence normale du champ
    }

    // Gestionnaire d'événements pour les boutons - ajouté pour résoudre l'erreur FXML ligne 50
    @FXML
    private void onButtonEnter(MouseEvent event) {
        System.out.println("🖱️ Souris entrée sur le bouton");
        // Vous pouvez ajouter ici du code pour changer l'apparence du bouton
        // Par exemple, changer la couleur de fond ou ajouter un effet visuel
    }

    // Gestionnaire optionnel pour quand la souris quitte le bouton
    @FXML
    private void onButtonExit(MouseEvent event) {
        System.out.println("🖱️ Souris sortie du bouton");
        // Code pour restaurer l'apparence normale du bouton
    }

    // Gestionnaire d'événements pour l'aide - ajouté pour résoudre l'erreur FXML ligne 58
    @FXML
    private void handleHelp(MouseEvent event) {
        System.out.println("ℹ️ Bouton d'aide cliqué");
        afficherInformation("Aide",
                "Instructions de connexion:\n\n" +
                        "• Saisissez votre nom d'utilisateur\n" +
                        "• Saisissez votre mot de passe\n" +
                        "• Cliquez sur 'Se connecter'\n\n" +
                        "En cas de problème, contactez l'administrateur.");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("✅ MainController configuré dans LoginController");
    }

    private void ouvrirDashboardEmploye() {
        try {
            System.out.println("🔍 Chargement du dashboard employé...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/GestionReservationView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Dashboard Employé");
            stage.setScene(new Scene(root));
            System.out.println("✅ Dashboard employé chargé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture du dashboard employé:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir le dashboard employé: " + e.getMessage());
        }
    }

    private void ouvrirDashboardClient(Utilisateur utilisateur) {
        try {
            System.out.println("🔍 Chargement du dashboard client...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/TableauDeBordClientView.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer les données du client
            Object controllerObj = loader.getController();
            if (controllerObj instanceof DashboardClientController) {
                DashboardClientController controller = (DashboardClientController) controllerObj;
                System.out.println("🔍 Recherche du client avec ID utilisateur: " + utilisateur.getId());

                try {
                    Client client = clientService.trouverClientParUtilisateur(utilisateur);
                    if (client != null) {
                        controller.setClientConnecte(client);
                        System.out.println("✅ Client configuré: " + utilisateur.getNom());
                    } else {
                        System.err.println("⚠️ Aucun client trouvé pour l'utilisateur: " + utilisateur.getId());
                        afficherErreur("Attention", "Profil client non trouvé");
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de la récupération du client: " + e.getMessage());
                    afficherErreur("Erreur", "Impossible de charger le profil client");
                    return;
                }
            } else {
                System.err.println("❌ Contrôleur du dashboard client non trouvé");
                afficherErreur("Erreur", "Erreur de configuration du contrôleur");
                return;
            }

            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Dashboard Client");
            stage.setScene(new Scene(root));
            System.out.println("✅ Dashboard client chargé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture du dashboard client:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir le dashboard client: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnulerReservation() {
        System.out.println("🔒 Fermeture de l'application");
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}