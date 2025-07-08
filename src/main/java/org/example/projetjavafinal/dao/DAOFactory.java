
package org.example.projetjavafinal.factory;

import org.example.projetjavafinal.dao.*;

public class DAOFactory {
    
    public static <T> GenericDAO<T> getDAO(Class<T> entityClass) {
        if (entityClass == Client.class) {
            return (GenericDAO<T>) new ClientDAO();
        } else if (entityClass == Vehicule.class) {
            return (GenericDAO<T>) new VehiculeDAO();
        } else if (entityClass == Chauffeur.class) {
            return (GenericDAO<T>) new ChauffeurDAO();
        } else if (entityClass == Reservation.class) {
            return (GenericDAO<T>) new ReservationDAO();
        }
        throw new IllegalArgumentException("Type d'entité non supporté : " + entityClass);
    }
}
