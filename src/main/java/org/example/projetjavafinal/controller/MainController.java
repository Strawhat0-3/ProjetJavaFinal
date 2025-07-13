package org.example.projetjavafinal.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projetjavafinal.service.AuthentificationService;
import org.example.projetjavafinal.util.HibernateUtil;

public class MainController extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialiser Hibernate au démarrage
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("✅ Application démarrée avec succès!");

            // Créer un utilisateur de test si nécessaire
            var authService = new AuthentificationService();
            authService.creerUtilisateurTest();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de l'application: " + e.getMessage());
            e.printStackTrace();
        }

        afficherLogin();
    }

    public void afficherLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            primaryStage.setTitle("Location de Voitures - Connexion");
            primaryStage.setScene(scene);
            primaryStage.show();

            LoginController loginController = loader.getController();
            loginController.setMainController(this);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la vue de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void afficherGestionVehicules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/GestionVehiculeView.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Gestion des Véhicules");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la vue de gestion des véhicules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void afficherGestionReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/GestionReservationView.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Gestion des Réservations");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la vue de gestion des réservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void afficherTableauDeBordClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/view/TableauDeBordClientView.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Tableau de Bord Client");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du tableau de bord client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        // Fermer proprement Hibernate lors de la fermeture de l'application
        HibernateUtil.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}