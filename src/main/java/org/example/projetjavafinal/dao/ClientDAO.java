package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Client;
import org.example.projetjavafinal.model.Utilisateur;
import org.example.projetjavafinal.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class ClientDAO {

    public Client save(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(client);
            transaction.commit();
            System.out.println("✅ Client sauvegardé avec ID: " + client.getId());
            return client;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur lors de la sauvegarde du client: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Client> findById(Long id) {
        if (id == null) {
            System.out.println("⚠️ findById appelé avec un ID null");
            return Optional.empty();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("🔍 Recherche client par ID: " + id);
            Client client = session.get(Client.class, id);

            if (client != null) {
                System.out.println("✅ Client trouvé: " + client.getNom() + " (Email: " + client.getEmail() + ")");
                return Optional.of(client);
            } else {
                System.out.println("⚠️ Aucun client trouvé avec l'ID: " + id);

                // Diagnostic : vérifier si l'ID existe vraiment
                diagnostiquerIdClient(session, id);

                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche par ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public Optional<Client> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("⚠️ findByEmail appelé avec un email vide");
            return Optional.empty();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("🔍 Recherche client par email: " + email);

            Query<Client> query = session.createQuery(
                    "FROM Client WHERE LOWER(email) = LOWER(:email)", Client.class);
            query.setParameter("email", email.trim());

            Optional<Client> result = query.uniqueResultOptional();

            if (result.isPresent()) {
                System.out.println("✅ Client trouvé par email: " + result.get().getNom());
            } else {
                System.out.println("⚠️ Aucun client trouvé avec l'email: " + email);
            }

            return result;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche par email " + email + ": " + e.getMessage());
            throw e;
        }
    }

    public Optional<Client> findByUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            System.out.println("⚠️ findByUtilisateur appelé avec un utilisateur null");
            return Optional.empty();
        }

        if (utilisateur.getId() == null) {
            System.out.println("⚠️ findByUtilisateur appelé avec un utilisateur sans ID");
            return Optional.empty();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("🔍 Recherche client pour utilisateur ID: " + utilisateur.getId() +
                    " (Nom: " + utilisateur.getNom() + ", Login: " + utilisateur.getLogin() + ")");

            // Première tentative : recherche directe par utilisateur_id
            String hql = "FROM Client c WHERE c.utilisateur.id = :userId";
            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("userId", utilisateur.getId());

            Optional<Client> result = query.uniqueResultOptional();

            if (result.isPresent()) {
                Client client = result.get();
                System.out.println("✅ Client trouvé par utilisateur: " + client.getNom() +
                        " (Client ID: " + client.getId() + ")");
                return result;
            } else {
                System.out.println("⚠️ Aucun client trouvé pour l'utilisateur ID: " + utilisateur.getId());

                // Diagnostic approfondi
                diagnostiquerUtilisateur(session, utilisateur);

                return Optional.empty();
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche par utilisateur " + utilisateur.getId() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Client> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("🔍 Récupération de tous les clients");
            List<Client> clients = session.createQuery("FROM Client", Client.class).list();
            System.out.println("📊 Nombre total de clients: " + clients.size());
            return clients;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération de tous les clients: " + e.getMessage());
            throw e;
        }
    }

    public void update(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client ou ID client null");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(client);
            transaction.commit();
            System.out.println("✅ Client mis à jour: " + client.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur lors de la mise à jour du client: " + e.getMessage());
            throw e;
        }
    }

    public void updatePointsFidelite(Long clientId, Integer points) {
        if (clientId == null || points == null) {
            throw new IllegalArgumentException("ID client ou points null");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                client.setPointsFidelite(points);
                session.merge(client);
                System.out.println("✅ Points fidélité mis à jour pour client " + clientId + ": " + points);
            } else {
                System.out.println("⚠️ Client introuvable pour mise à jour points: " + clientId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur lors de la mise à jour des points: " + e.getMessage());
            throw e;
        }
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID client null");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, id);
            if (client != null) {
                session.remove(client);
                System.out.println("✅ Client supprimé: " + id);
            } else {
                System.out.println("⚠️ Client introuvable pour suppression: " + id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur lors de la suppression du client: " + e.getMessage());
            throw e;
        }
    }

    // === MÉTHODES DE DIAGNOSTIC ===

    private void diagnostiquerIdClient(Session session, Long clientId) {
        try {
            System.out.println("🔍 === DIAGNOSTIC CLIENT ID " + clientId + " ===");

            // Vérifier si l'ID existe dans la table clients
            Query<Long> countQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Client WHERE id = :id", Long.class);
            countQuery.setParameter("id", clientId);
            Long count = countQuery.uniqueResult();

            System.out.println("📊 Nombre de clients avec ID " + clientId + ": " + count);

            // Lister tous les IDs clients existants
            Query<Long> idsQuery = session.createQuery("SELECT id FROM Client ORDER BY id", Long.class);
            List<Long> ids = idsQuery.getResultList();
            System.out.println("📋 IDs clients existants: " + ids);

            // Vérifier s'il y a un utilisateur avec cet ID
            Query<Long> userCountQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Utilisateur WHERE id = :id", Long.class);
            userCountQuery.setParameter("id", clientId);
            Long userCount = userCountQuery.uniqueResult();

            System.out.println("👤 Utilisateur avec ID " + clientId + " existe: " + (userCount > 0));

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du diagnostic: " + e.getMessage());
        }
    }

    private void diagnostiquerUtilisateur(Session session, Utilisateur utilisateur) {
        try {
            System.out.println("🔍 === DIAGNOSTIC UTILISATEUR " + utilisateur.getId() + " ===");

            // Vérifier si l'utilisateur existe vraiment
            Utilisateur userFromDb = session.get(Utilisateur.class, utilisateur.getId());
            if (userFromDb != null) {
                System.out.println("✅ Utilisateur trouvé en base: " + userFromDb.getNom());
                System.out.println("📧 Email/Login: " + userFromDb.getLogin());
                System.out.println("🔑 Rôle: " + userFromDb.getRole());
            } else {
                System.out.println("❌ Utilisateur introuvable en base!");
            }

            // Chercher des clients avec le même email
            if (utilisateur.getLogin() != null) {
                Query<Client> emailQuery = session.createQuery(
                        "FROM Client WHERE LOWER(email) = LOWER(:email)", Client.class);
                emailQuery.setParameter("email", utilisateur.getLogin());
                List<Client> clientsWithEmail = emailQuery.getResultList();

                System.out.println("📧 Clients avec email '" + utilisateur.getLogin() + "': " + clientsWithEmail.size());
                for (Client c : clientsWithEmail) {
                    System.out.println("  - Client ID: " + c.getId() + ", Utilisateur associé: " +
                            (c.getUtilisateur() != null ? c.getUtilisateur().getId() : "null"));
                }
            }

            // Lister tous les clients avec leurs utilisateurs associés
            Query<Object[]> allClientsQuery = session.createQuery(
                    "SELECT c.id, c.nom, c.email, u.id, u.nom FROM Client c LEFT JOIN c.utilisateur u", Object[].class);
            List<Object[]> allClients = allClientsQuery.getResultList();

            System.out.println("📋 Tous les clients et leurs utilisateurs:");
            for (Object[] row : allClients) {
                System.out.println("  - Client ID: " + row[0] + ", Nom: " + row[1] +
                        ", Email: " + row[2] + ", User ID: " + row[3] + ", User Nom: " + row[4]);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du diagnostic utilisateur: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour afficher l'état complet de la base
    public void afficherEtatComplet() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n🔍 === ÉTAT COMPLET DE LA BASE ===");

            // Compter utilisateurs
            Query<Long> userCountQuery = session.createQuery("SELECT COUNT(*) FROM Utilisateur", Long.class);
            Long userCount = userCountQuery.uniqueResult();
            System.out.println("👥 Total utilisateurs: " + userCount);

            // Compter clients
            Query<Long> clientCountQuery = session.createQuery("SELECT COUNT(*) FROM Client", Long.class);
            Long clientCount = clientCountQuery.uniqueResult();
            System.out.println("🏢 Total clients: " + clientCount);

            // Utilisateurs CLIENT sans client associé
            Query<Long> orphanUsersQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Utilisateur u WHERE u.role = 'CLIENT' AND u.client IS NULL", Long.class);
            Long orphanUsers = orphanUsersQuery.uniqueResult();
            System.out.println("👤 Utilisateurs CLIENT sans client: " + orphanUsers);

            // Clients sans utilisateur associé
            Query<Long> orphanClientsQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Client c WHERE c.utilisateur IS NULL", Long.class);
            Long orphanClients = orphanClientsQuery.uniqueResult();
            System.out.println("🏢 Clients sans utilisateur: " + orphanClients);

            System.out.println("=== FIN ÉTAT COMPLET ===\n");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage de l'état: " + e.getMessage());
        }
    }
}