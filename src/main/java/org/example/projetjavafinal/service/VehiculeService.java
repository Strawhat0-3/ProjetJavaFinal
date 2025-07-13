package org.example.projetjavafinal.service;

import org.example.projetjavafinal.Observable;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.DAOFactory;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.Vehicule;

import java.util.ArrayList;
import java.util.List;

public class VehiculeService implements Observable {
    private final VehiculeDAO vehiculeDAO;
    private final List<Observer> observers = new ArrayList<>();

    // CORRECTION : Constructeur sans paramètre utilisant DAOFactory
    public VehiculeService() {
        this.vehiculeDAO = (VehiculeDAO) DAOFactory.getDAO(Vehicule.class);
    }

    // CORRECTION : Constructeur avec paramètre utilisant le DAO fourni
    public VehiculeService(VehiculeDAO vehiculeDAO) {
        this.vehiculeDAO = vehiculeDAO;
    }

    public List<Vehicule> trouverVehiculesDisponibles() {
        return vehiculeDAO.findByDisponible(true);
    }

    public List<Vehicule> trouverVehiculeParCategorie(Vehicule.Categorie categorie) {
        return vehiculeDAO.findByCategorie(categorie);
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String type, Object data) {
        for (Observer observer : observers) {
            observer.update(type, data);
        }
    }

    public void ajouterVehicule(Vehicule vehicule) {
        vehiculeDAO.save(vehicule);
        notifyObservers("VEHICULE_LIST_UPDATED", trouverTousLesVehicules());
    }

    public void modifierVehicule(Vehicule vehicule) {
        vehiculeDAO.update(vehicule);
        notifyObservers("VEHICULE_LIST_UPDATED", trouverTousLesVehicules());
    }

    public void supprimerVehicule(Long id) {
        vehiculeDAO.deleteById(id);
        notifyObservers("VEHICULE_LIST_UPDATED", trouverTousLesVehicules());
    }

    public List<Vehicule> trouverTousLesVehicules() {
        return vehiculeDAO.findAll();
    }
}