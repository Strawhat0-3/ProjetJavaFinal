package org.example.projetjavafinal;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String type, Object data);
}