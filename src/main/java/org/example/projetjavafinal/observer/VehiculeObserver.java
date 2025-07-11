package org.example.projetjavafinal.observer;

import org.example.projetjavafinal.Observer;
import javafx.scene.control.TableView;
import org.example.projetjavafinal.model.Vehicule;

import java.util.List;

public class VehiculeObserver implements Observer {
    private final TableView<Vehicule> tableView;

    public VehiculeObserver(TableView<Vehicule> tableView) {
        this.tableView = tableView;
    }

    @Override
    public void update(String type, Object data) {
        if ("VEHICULE_LIST_UPDATED".equals(type)) {
            @SuppressWarnings("unchecked")
            List<Vehicule> vehicules = (List<Vehicule>) data;
            tableView.getItems().setAll(vehicules);
        }
    }
}
