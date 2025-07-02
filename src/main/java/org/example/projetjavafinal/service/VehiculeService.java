package org.example.projetjavafinal.service;

import org.example.projetjavafinal.Observable;
import org.example.projetjavafinal.Observer;
import org.example.projetjavafinal.dao.VehiculeDAO;
import org.example.projetjavafinal.model.Vehicule;

import java.util.ArrayList;
import java.util.List;

public class VehiculeService implements Observable {
    private final VehiculeDAO vehiculeDAO;
    private final List<Observer> observers = new ArrayList<>();

    public VehiculeService(VehiculeDAO vehiculeDAO) {
        this.vehiculeDAO = vehiculeDAO;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    // Modification des méthodes existantes pour notifier les observateurs
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        Vehicule result = vehiculeDAO.save(vehicule);
        notifyObservers();
        return result;
    }

    public void mettreAJourVehicule(Vehicule vehicule) {
        vehiculeDAO.update(vehicule);
        notifyObservers();
    }

    public void supprimerVehicule(Long id) {
        vehiculeDAO.deleteById(id);
        notifyObservers();
    }
}