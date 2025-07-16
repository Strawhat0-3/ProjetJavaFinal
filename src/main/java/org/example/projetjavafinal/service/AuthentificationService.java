package org.example.projetjavafinal.service;

import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthentificationService {

    public Utilisateur authentifier(String login, String motDePasse) {
        System.out.println("🔍 [AuthService] Début de l'authentification");
        System.out.println("🔍 [AuthService] Login: " + login);

        Session session = null;
        try {
            // Obtenir la session Hibernate
            session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("✅ [AuthService] Session Hibernate ouverte");

            // Rechercher l'utilisateur par login
            System.out.println("🔍 [AuthService] Recherche de l'utilisateur dans la base...");
            Query<Utilisateur> query = session.createQuery(
                    "FROM Utilisateur WHERE login = :login",
                    Utilisateur.class
            );
            query.setParameter("login", login);

            Utilisateur utilisateur = query.uniqueResult();

            if (utilisateur == null) {
                System.out.println("❌ [AuthService] Aucun utilisateur trouvé avec le login: " + login);

                // Debug: Lister tous les utilisateurs
                System.out.println("🔍 [AuthService] Liste de tous les utilisateurs:");
                Query<Utilisateur> allUsersQuery = session.createQuery("FROM Utilisateur", Utilisateur.class);
                List<Utilisateur> allUsers = allUsersQuery.getResultList();

                if (allUsers.isEmpty()) {
                    System.out.println("⚠️ [AuthService] Aucun utilisateur dans la base de données!");
                } else {
                    for (Utilisateur u : allUsers) {
                        System.out.println("   - Login: '" + u.getLogin() + "', Role: " + u.getRole());
                    }
                }

                return null;
            }

            System.out.println("✅ [AuthService] Utilisateur trouvé: " + utilisateur.getLogin());
            System.out.println("🔍 [AuthService] Role: " + utilisateur.getRole());

            // Vérifier le mot de passe
            System.out.println("🔍 [AuthService] Vérification du mot de passe...");

            // Si le mot de passe est hashé avec BCrypt
            if (utilisateur.getMotDePasse().startsWith("$2a$") ||
                    utilisateur.getMotDePasse().startsWith("$2b$") ||
                    utilisateur.getMotDePasse().startsWith("$2y$")) {

                System.out.println("🔍 [AuthService] Mot de passe hashé détecté, vérification BCrypt...");
                boolean motDePasseValide = BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse());

                if (motDePasseValide) {
                    System.out.println("✅ [AuthService] Mot de passe BCrypt valide");
                    return utilisateur;
                } else {
                    System.out.println("❌ [AuthService] Mot de passe BCrypt invalide");
                    return null;
                }
            } else {
                // Comparaison directe (non sécurisée, à des fins de debug)
                System.out.println("🔍 [AuthService] Mot de passe en clair détecté");
                System.out.println("🔍 [AuthService] Mot de passe stocké: " + utilisateur.getMotDePasse());
                System.out.println("🔍 [AuthService] Mot de passe saisi: " + motDePasse);

                if (utilisateur.getMotDePasse().equals(motDePasse)) {
                    System.out.println("✅ [AuthService] Mot de passe en clair valide");
                    return utilisateur;
                } else {
                    System.out.println("❌ [AuthService] Mot de passe en clair invalide");
                    return null;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ [AuthService] Erreur lors de l'authentification:");
            System.err.println("Type: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                try {
                    session.close();
                    System.out.println("🔒 [AuthService] Session fermée");
                } catch (Exception e) {
                    System.err.println("❌ [AuthService] Erreur lors de la fermeture de session: " + e.getMessage());
                }
            }
        }
    }

}