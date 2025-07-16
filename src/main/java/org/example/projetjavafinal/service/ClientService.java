package org.example.projetjavafinal.service;

import org.example.projetjavafinal.dao.ClientDAO;
import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.model.Utilisateur.Role;
import org.example.projetjavafinal.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Inscrit un nouveau client associé à un utilisateur
     */
    public Client inscrireClient(Utilisateur utilisateur, String email, String telephone) {
        if (utilisateur == null || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Paramètres invalides pour la création de client");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Créer le client
            Client client = new Client();
            client.setNom(utilisateur.getNom());
            client.setEmail(email);
            client.setTelephone(telephone != null ? telephone : "Non spécifié");
            client.setPointsFidelite(0);
            client.setUtilisateur(utilisateur);

            // Synchroniser la relation bidirectionnelle
            utilisateur.setClient(client);

            // Sauvegarder dans la même transaction
            session.persist(client);
            session.merge(utilisateur); // Mettre à jour l'utilisateur aussi

            transaction.commit();
            System.out.println("Client créé avec succès - ID: " + client.getId());
            return client;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Erreur lors de la création du client: " + e.getMessage());
            throw new RuntimeException("Impossible de créer le client", e);
        }
    }
    // ✅ CORRECT method to find client by user relationship using Hibernate
    public Client trouverClientParUtilisateur(Utilisateur utilisateur) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Client c WHERE c.utilisateur.id = :utilisateurId";
            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("utilisateurId", utilisateur.getId());

            Client client = query.uniqueResult();

            if (client != null) {
                System.out.println("✅ Client trouvé: ID=" + client.getId() + " pour utilisateur ID=" + utilisateur.getId());
                return client;
            }

            System.out.println("⚠️ Aucun client trouvé pour l'utilisateur ID: " + utilisateur.getId());
            return null;

        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche du client: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }



    // ✅ FIXED method to find or create client using Hibernate
    public Client trouverOuCreerClient(Utilisateur utilisateur) {
        // First, try to find existing client by user relationship
        Client client = trouverClientParUtilisateur(utilisateur);

        if (client != null) {
            System.out.println("✅ Client existant trouvé: ID=" + client.getId());
            return client;
        }

        // If not found, create new client
        System.out.println("⚠️ Client non trouvé, création d'un nouveau client pour l'utilisateur: " + utilisateur.getNom());

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Create new client
            client = new Client();
            client.setNom(utilisateur.getNom());
            client.setEmail(client.getEmail());
            client.setTelephone(client.getTelephone());
            client.setPointsFidelite(0); // Points fidélité initiaux
            client.setUtilisateur(utilisateur);

            session.save(client);
            transaction.commit();

            System.out.println("✅ Nouveau client créé: ID=" + client.getId());
            return client;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Erreur lors de la création du client: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création du client", e);
        } finally {
            session.close();
        }
    }


    // ✅ FIXED diagnostic method using Hibernate
    public void diagnostiquerClient(Long clientId) {
        System.out.println("=== DIAGNOSTIC CLIENT ===");
        System.out.println("Recherche du client avec ID: " + clientId);

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Client client = session.get(Client.class, clientId);

            if (client != null) {
                System.out.println("✅ Client trouvé:");
                System.out.println("  - Client ID: " + client.getId());
                System.out.println("  - Nom: " + client.getNom());
                System.out.println("  - Email: " + client.getEmail());
                System.out.println("  - Téléphone: " + client.getTelephone());
                System.out.println("  - Points: " + client.getPointsFidelite());

                if (client.getUtilisateur() != null) {
                    System.out.println("  - Utilisateur ID: " + client.getUtilisateur().getId());
                    System.out.println("  - Utilisateur nom: " + client.getUtilisateur().getNom());
                    System.out.println("  - Utilisateur email: " + client.getEmail());
                } else {
                    System.out.println("  - ⚠️ Aucun utilisateur associé");
                }
            } else {
                System.out.println("❌ Aucun client trouvé avec l'ID: " + clientId);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du diagnostic: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // ✅ Alternative diagnostic method by user ID using Hibernate
    public void diagnostiquerClientParUtilisateur(Long utilisateurId) {
        System.out.println("=== DIAGNOSTIC CLIENT PAR UTILISATEUR ===");
        System.out.println("Recherche du client pour l'utilisateur ID: " + utilisateurId);

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Client c LEFT JOIN FETCH c.utilisateur WHERE c.utilisateur.id = :utilisateurId";
            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("utilisateurId", utilisateurId);

            Client client = query.uniqueResult();

            if (client != null) {
                System.out.println("✅ Client trouvé:");
                System.out.println("  - Client ID: " + client.getId());
                System.out.println("  - Nom: " + client.getNom());
                System.out.println("  - Email: " + client.getEmail());
                System.out.println("  - Téléphone: " + client.getTelephone());
                System.out.println("  - Points: " + client.getPointsFidelite());
                System.out.println("  - Utilisateur ID: " + client.getUtilisateur().getId());
            } else {
                System.out.println("❌ Aucun client trouvé pour l'utilisateur ID: " + utilisateurId);

                // Also check if the user exists
                Utilisateur utilisateur = session.get(Utilisateur.class, utilisateurId);
                if (utilisateur != null) {
                    System.out.println("✅ Utilisateur existe: " + utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
                } else {
                    System.out.println("❌ Utilisateur n'existe pas avec l'ID: " + utilisateurId);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du diagnostic: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // ✅ Additional method to migrate missing clients using Hibernate
    public void migrerClientsManquants() {
        System.out.println("=== MIGRATION DES CLIENTS MANQUANTS ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Find all users with CLIENT role who don't have a client record
            String hql = "FROM Utilisateur u WHERE u.role = :role AND u.id NOT IN " +
                    "(SELECT c.utilisateur.id FROM Client c WHERE c.utilisateur IS NOT NULL)";
            Query<Utilisateur> query = session.createQuery(hql, Utilisateur.class);
            query.setParameter("role", Utilisateur.Role.CLIENT);

            List<Utilisateur> utilisateursSansClient = query.getResultList();

            System.out.println("Utilisateurs CLIENT sans profil client: " + utilisateursSansClient.size());

            for (Utilisateur utilisateur : utilisateursSansClient) {
                System.out.println("Création du client pour: " + utilisateur.getNom() + " (ID: " + utilisateur.getId() + ")");

                Client client = new Client();
                client.setNom(utilisateur.getNom());
                client.setEmail(client.getEmail());
                client.setTelephone(client.getTelephone());
                client.setPointsFidelite(0);
                client.setUtilisateur(utilisateur);

                session.save(client);
            }

            transaction.commit();
            System.out.println("✅ Migration terminée: " + utilisateursSansClient.size() + " clients créés");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Erreur lors de la migration: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    public List<Client> trouverTousLesClients() {
        return clientDAO.findAll();
    }
}