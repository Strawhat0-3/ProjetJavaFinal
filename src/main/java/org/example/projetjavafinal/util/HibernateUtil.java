package org.example.projetjavafinal.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    private static void buildSessionFactory() {
        try {
            System.out.println("🔧 Initialisation de Hibernate...");

            // Créer la configuration
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            System.out.println("📋 Configuration Hibernate chargée depuis hibernate.cfg.xml");

            // Ajouter explicitement les entités si elles ne sont pas dans le XML
            try {
                configuration.addAnnotatedClass(org.example.projetjavafinal.model.Utilisateur.class);
                System.out.println("✅ Entité Utilisateur ajoutée");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'ajout de l'entité Utilisateur: " + e.getMessage());
            }

            try {
                configuration.addAnnotatedClass(org.example.projetjavafinal.model.Client.class);
                System.out.println("✅ Entité Client ajoutée");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'ajout de l'entité Client: " + e.getMessage());
            }

            try {
                configuration.addAnnotatedClass(org.example.projetjavafinal.model.Vehicule.class);
                System.out.println("✅ Entité Vehicule ajoutée");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'ajout de l'entité Vehicule: " + e.getMessage());
            }

            try {
                configuration.addAnnotatedClass(org.example.projetjavafinal.model.Reservation.class);
                System.out.println("✅ Entité Reservation ajoutée");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'ajout de l'entité Reservation: " + e.getMessage());
            }

            // AJOUT : Entité Chauffeur si elle existe
            try {
                configuration.addAnnotatedClass(org.example.projetjavafinal.model.Chauffeur.class);
                System.out.println("✅ Entité Chauffeur ajoutée");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de l'ajout de l'entité Chauffeur: " + e.getMessage());
                System.err.println("ℹ️ Si vous n'avez pas de classe Chauffeur, c'est normal");
            }

            // Créer le service registry
            serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            System.out.println("🔧 ServiceRegistry créé avec succès");

            // Créer la SessionFactory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            System.out.println("✅ SessionFactory créée avec succès !");

            // Tester la connexion
            testConnection();

        } catch (Exception e) {
            System.err.println("❌ Erreur détaillée lors de la création de la SessionFactory :");
            System.err.println("Type d'erreur : " + e.getClass().getSimpleName());
            System.err.println("Message : " + e.getMessage());

            // Afficher la cause racine si elle existe
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("Cause : " + cause.getMessage());

                // Afficher plus de détails sur la cause
                if (cause.getCause() != null) {
                    System.err.println("Cause racine : " + cause.getCause().getMessage());
                }
            }

            e.printStackTrace();

            // Nettoyer les ressources en cas d'erreur
            cleanup();

            // Lancer une exception avec un message plus clair
            throw new RuntimeException("Impossible d'initialiser Hibernate. Vérifiez votre configuration de base de données.", e);
        }
    }

    private static void testConnection() {
        try {
            if (sessionFactory != null) {
                // Tester la connexion en ouvrant et fermant une session
                var session = sessionFactory.openSession();
                System.out.println("🔍 Test de la session Hibernate...");

                // Tester une requête simple plus basique
                try {
                    var query = session.createQuery("SELECT COUNT(*) FROM Utilisateur", Long.class);
                    Long count = query.uniqueResult();
                    System.out.println("📊 Nombre d'utilisateurs dans la base: " + count);
                } catch (Exception e) {
                    System.err.println("⚠️ Impossible de compter les utilisateurs, mais la connexion fonctionne");
                    System.err.println("Détail : " + e.getMessage());
                }

                session.close();
                System.out.println("✅ Test de connexion à la base de données réussi");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du test de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    System.out.println("⚠️ SessionFactory est null, tentative de création...");
                    buildSessionFactory();
                }
            }
        }

        if (sessionFactory == null) {
            throw new RuntimeException("❌ Impossible de créer la SessionFactory. Vérifiez la configuration de la base de données.");
        }

        return sessionFactory;
    }

    public static void shutdown() {
        try {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
                System.out.println("🔒 SessionFactory fermée avec succès");
            }

            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                System.out.println("🔒 ServiceRegistry détruit avec succès");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la fermeture de Hibernate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void cleanup() {
        try {
            if (sessionFactory != null) {
                sessionFactory.close();
                sessionFactory = null;
            }

            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                serviceRegistry = null;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du nettoyage des ressources: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour vérifier si Hibernate est initialisé
    public static boolean isInitialized() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }
}