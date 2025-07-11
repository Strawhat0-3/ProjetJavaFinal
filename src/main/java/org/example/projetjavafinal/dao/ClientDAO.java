package org.example.projetjavafinal.dao;

import org.example.projetjavafinal.model.Client;
import org.hibernate.Session;
import java.util.Optional;


public class ClientDAO extends GenericDAOImpl<Client> {
    
    public Optional<Client> findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Client c WHERE c.email = :email", 
                Client.class)
                .setParameter("email", email)
                .uniqueResultOptional();
        }
    }
    
    public void updatePointsFidelite(Long clientId, Integer points) {
        try (Session session = getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                client.setPointsFidelite(client.getPointsFidelite() + points);
                session.merge(client);
            }
            transaction.commit();
        }
    }
}