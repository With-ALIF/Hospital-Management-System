package com.hospital.ui;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.*;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AppointmentForm {
    public static VBox build(AppState state) {
        VBox card = new VBox(12);
        card.setMinWidth(360);
        card.setMaxWidth(380);
        card.getStyleClass().add("card");
        Label title = new Label("Book New Appointment");
        title.getStyleClass().add("section-title");
        TextField idField = UIHelper.createStyledTextField("e.g. A-1002");
        idField.setText("A-" + (1001 + state.appointments.size()));
        ComboBox<Patient> patientBox = new ComboBox<>(state.patients);
        patientBox.setMaxWidth(Double.MAX_VALUE);
        patientBox.getStyleClass().add("combo-box");
        if (!state.patients.isEmpty()) patientBox.setValue(state.patients.get(0));
        patientBox.setConverter(EntityConverters.patient());
        ComboBox<Doctor> doctorBox = new ComboBox<>(state.doctors);
        doctorBox.setMaxWidth(Double.MAX_VALUE);
        doctorBox.getStyleClass().add("combo-box");
        if (!state.doctors.isEmpty()) doctorBox.setValue(state.doctors.get(0));
        doctorBox.setConverter(EntityConverters.doctor());
        Label dutyLabel = new Label();
        dutyLabel.setWrapText(true);
        dutyLabel.setStyle("-fx-background-color: rgba(37, 99, 235, 0.1); -fx-text-fill: #60a5fa; -fx-padding: 8 12; -fx-background-radius: 6; -fx-font-size: 12px; -fx-font-weight: bold;");
        Runnable updateDuty = () -> {
            Doctor d = doctorBox.getValue();
            dutyLabel.setText("Duty Hours: " + (d != null ? d.getDutyScheduleString() : "None"));
        };
        updateDuty.run();
        doctorBox.valueProperty().addListener((obs, o, n) -> updateDuty.run());
        DatePicker datePicker = new DatePicker(LocalDate.of(2026, 9, 19));
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.getStyleClass().add("date-picker");
        TextField timeField = UIHelper.createStyledTextField("HH:mm (e.g. 10:30)");
        timeField.setText("10:30");
        TextField reasonField = UIHelper.createStyledTextField("e.g. Chest Pain, Regular Checkup");
        ComboBox<AppointmentStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(AppointmentStatus.values()));
        statusBox.setValue(AppointmentStatus.SCHEDULED);
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.getStyleClass().add("combo-box");
        Button bookBtn = new Button("Confirm Booking");
        bookBtn.getStyleClass().add("btn-warning");
        bookBtn.setMaxWidth(Double.MAX_VALUE);
        Button clearBtn = new Button("Reset Form");
        clearBtn.getStyleClass().add("button");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        bookBtn.setOnAction(e -> onBook(state, idField, patientBox, doctorBox, datePicker, timeField, reasonField, statusBox));
        clearBtn.setOnAction(e -> {
            idField.setText("A-" + (1001 + state.appointments.size()));
            reasonField.clear();
            timeField.setText("10:30");
        });
        card.getChildren().addAll(title, UIHelper.createFormField("Appointment ID", idField),
                UIHelper.createFormField("Select Patient", patientBox), UIHelper.createFormField("Select Doctor", doctorBox),
                dutyLabel, UIHelper.createFormField("Appointment Date", datePicker),
                UIHelper.createFormField("Appointment Time (HH:mm)", timeField), UIHelper.createFormField("Reason for Visit", reasonField),
                UIHelper.createFormField("Initial Status", statusBox), bookBtn, clearBtn);
        return card;
    }

    private static void onBook(AppState state, TextField idF, ComboBox<Patient> pBox, ComboBox<Doctor> dBox, DatePicker dPicker, TextField tF, TextField rF, ComboBox<AppointmentStatus> sBox) {
        String aid = idF.getText().trim(), tText = tF.getText().trim(), reason = rF.getText().trim();
        Patient p = pBox.getValue(); Doctor d = dBox.getValue(); LocalDate date = dPicker.getValue();
        if (aid.isEmpty() || p == null || d == null || date == null || tText.isEmpty()) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all mandatory appointment fields.");
            return;
        }
        try {
            LocalTime time = LocalTime.parse(tText, SlotParser.TIME_FORMATTER);
            Appointment apt = new Appointment(aid, p, d, date, time, reason, sBox.getValue());
            state.service.bookAppointment(apt);
            state.appointments.add(apt);
            state.notifyChange();
            idF.setText("A-" + (1001 + state.appointments.size()));
            rF.clear();
            UIHelper.showAlert(Alert.AlertType.INFORMATION, "Appointment Booked!", "Successfully booked appointment " + aid + ".");
        } catch (DateTimeParseException ex) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Invalid Time Format", "Please enter time in 24-hour format HH:mm (e.g. 10:30).");
        } catch (InvalidDataException ex) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Booking Error", ex.getMessage());
        }
    }
}
