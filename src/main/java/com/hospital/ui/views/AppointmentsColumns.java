package com.hospital.ui.views;

import com.hospital.model.Appointment;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

final class AppointmentsColumns {
    private AppointmentsColumns() {
    }

    static TableColumn<Appointment, Void> actionCol() {
        TableColumn<Appointment, Void> cAct = new TableColumn<>("Actions");
        cAct.setPrefWidth(96);
        cAct.setCellFactory(col -> new TableCell<>() {
            private final Button view = new Button("View");
            {
                view.getStyleClass().addAll("btn-ghost", "btn-small");
                view.setOnAction(e -> AppointmentsDialogs.show(
                        getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void it, boolean emp) {
                super.updateItem(it, emp);
                setGraphic(emp ? null : new HBox(view));
            }
        });
        return cAct;
    }

    static TableColumn<Appointment, String> statusCol() {
        TableColumn<Appointment, String> cStatus = new TableColumn<>("Status");
        cStatus.setPrefWidth(102);
        cStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String it, boolean emp) {
                super.updateItem(it, emp);
                if (emp || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Appointment a = (Appointment) getTableRow().getItem();
                setGraphic(com.hospital.ui.components.BadgeFactory.status(
                        a.getStatus() != null ? a.getStatus().name() : ""));
                setText(null);
            }
        });
        return cStatus;
    }
}
