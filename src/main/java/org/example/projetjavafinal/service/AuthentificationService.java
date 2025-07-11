package org.example.projetjavafinal.service;

import org.example.projetjavafinal.exception.AuthentificationException;
import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.util.HibernateUtil;
import org.hibernate.Session;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt; // Ajoutez cette dépendance

public class AuthentificationService {
    
    public Utilisateur authentifier(String login, String password) {
        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            throw new AuthentificationException("Le login et le mot de passe sont requis");
        }
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Recherche de l'utilisateur par login
            Optional<Utilisateur> utilisateur = session.createQuery(
                    "FROM Utilisateur u WHERE u.login = :login", Utilisateur.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();

            if (utilisateur.isPresent()) {
                // Vérification avec BCrypt
                if (BCrypt.checkpw(password, utilisateur.get().getMotDePasse())) {
                    return utilisateur.get();
                }
            }
            
            throw new AuthentificationException("Login ou mot de passe incorrect");
        } catch (Exception e) {
            throw new AuthentificationException("Erreur lors de l'authentification: " + e.getMessage());
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }
}