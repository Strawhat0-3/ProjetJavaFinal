package org.example.projetjavafinal.observer;

import org.example.projetjavafinal.Observer;
import javafx.scene.control.TableView;
import org.example.projetjavafinal.model.Reservation;

public class ReservationObserver implements Observer {
    private final TableView<Reservation> tableView;

    public ReservationObserver(TableView<Reservation> tableView) {
        this.tableView = tableView;
    }

    @Override
    public void update(String type, Object data) {
        if ("RESERVATION_UPDATED".equals(type)) {
            @SuppressWarnings("unchecked")
            List<Reservation> reservations = (List<Reservation>) data;
            tableView.getItems().setAll(reservations);
        }
    }
}
