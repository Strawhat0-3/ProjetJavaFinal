package org.example.projetjavafinal;

// Interface Observable
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
