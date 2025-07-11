package org.example.projetjavafinal.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController extends Application {
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        afficherLogin();
    }
    
    public void afficherLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavafinal/LoginView.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            primaryStage.setTitle("Location de Voitures - Connexion");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            LoginController loginController = loader.getController();
            loginController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void afficherGestionVehicules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionVehicules.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Gestion des Véhicules");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}