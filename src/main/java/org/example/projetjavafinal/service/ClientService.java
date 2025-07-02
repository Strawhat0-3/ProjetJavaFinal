package org.example.projetjavafinal.service;

import org.example.projetjavafinal.dao.ClientDAO;
import org.example.projetjavafinal.model.Client;

import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public Client ajouterClient(Client client) {
        return clientDAO.save(client);
    }

    public Optional<Client> trouverClientParId(Long id) {
        return clientDAO.findById(id);
    }

    public Optional<Client> trouverClientParEmail(String email) {
        return clientDAO.findByEmail(email);
    }

    public List<Client> trouverTousLesClients() {
        return clientDAO.findAll();
    }

    public void supprimerClient(Long id) {
        clientDAO.deleteById(id);
    }

    public void mettreAJourClient(Client client) {
        clientDAO.update(client);
    }

    public void ajouterPointsFidelite(Long clientId, Integer points) {
        clientDAO.updatePointsFidelite(clientId, points);
    }
}