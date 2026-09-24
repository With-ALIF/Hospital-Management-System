package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PublicAppointmentRequestView {

    public static VBox build(AppState state) {
        Label title = PublicDepartments.sectionTitle("Request an appointment");
        Label sub = PublicDepartments.sectionSub(
                "Fill in your details — staff will confirm your slot. No login needed.");

        TextField nameField = new TextField();
        nameField.setPromptText("Full name, e.g. Rahim Uddin");
        nameField.getStyleClass().add("text-field");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone, e.g. 01700000000");
        phoneField.getStyleClass().addAll("text-field", "tnum");

        TextField ageField = new TextField();
        ageField.setPromptText("Age (optional)");
        ageField.getStyleClass().addAll("text-field", "tnum");

        ComboBox<String> doctorBox = new ComboBox<>(FXCollections.observableArrayList(doctorOptions(state)));
        doctorBox.setPromptText("Select doctor");
        doctorBox.setMaxWidth(Double.MAX_VALUE);

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> timeBox = new ComboBox<>(
                FXCollections.observableArrayList(timeSlots()));
        timeBox.setPromptText("Select time");
        timeBox.setMaxWidth(Double.MAX_VALUE);

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Reason for visit, e.g. Fever and checkup");
        reasonArea.setPrefRowCount(2);
        reasonArea.setWrapText(true);

        Label error = new Label();
        error.setStyle("-fx-font-size:12px;-fx-text-fill:#B91C1C;-fx-font-weight:600;");
        error.setWrapText(true);
        error.setVisible(false);
        error.setManaged(false);

        Label success = new Label();
        success.setStyle("-fx-font-size:12.5px;-fx-text-fill:#15803D;-fx-font-weight:700;");
        success.setWrapText(true);
        success.setVisible(false);
        success.setManaged(false);

        Button submit = new Button("Send appointment request");
        submit.getStyleClass().add("btn-primary");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> {
            error.setVisible(false);
            error.setManaged(false);
            success.setVisible(false);
            success.setManaged(false);
            try {
                Appointment created = submitRequest(state,
                        nameField.getText(), phoneField.getText(), ageField.getText(),
                        doctorBox.getValue(), datePicker.getValue(),
                        timeBox.getValue(), reasonArea.getText());
                success.setText("Request received! Your appointment ID is " + created.getId()
                        + " (" + created.getFormattedDate() + " at " + created.getFormattedTime()
                        + "). Staff will confirm shortly.");
                success.setVisible(true);
                success.setManaged(true);
                nameField.clear();
                phoneField.clear();
                ageField.clear();
                timeBox.setValue(null);
                reasonArea.clear();
            } catch (Exception ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
                error.setManaged(true);
            }
        });

        VBox left = new VBox(6,
                fieldLabel("Full name *"), nameField,
                fieldLabel("Phone *"), phoneField,
                fieldLabel("Age"), ageField);
        VBox right = new VBox(6,
                fieldLabel("Doctor *"), doctorBox,
                fieldLabel("Preferred date *"), datePicker,
                fieldLabel("Preferred time *"), timeBox);
        left.setPrefWidth(300);
        right.setPrefWidth(300);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        FlowPane form = new FlowPane(16, 12);
        form.setMaxWidth(Double.MAX_VALUE);
        form.getChildren().addAll(left, right);

        VBox card = new VBox(12,
                new VBox(2, title, sub),
                form,
                new VBox(4, fieldLabel("Reason for visit *"), reasonArea),
                error, success, submit);
        card.setId("appointment-request");
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#10263B;-fx-font-weight:600;");
        return l;
    }

    private static List<String> doctorOptions(AppState state) {
        List<Doctor> doctors;
        try {
            doctors = state.doctorService.getAllDoctors();
        } catch (Exception ex) {
            doctors = List.of();
        }
        if (doctors == null || doctors.isEmpty()) {
            doctors = state.doctors;
        }
        return doctors.stream()
                .map(d -> d.getId() + " — " + d.getName()
                        + (d.getSpecialization() != null && !d.getSpecialization().isBlank()
                                ? " (" + d.getSpecialization() + ")" : ""))
                .sorted()
                .toList();
    }

    private static List<String> timeSlots() {
        return List.of("09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
                "19:00", "19:30", "20:00");
    }

    private static Appointment submitRequest(AppState state,
                                             String name, String phone, String ageText,
                                             String doctorOption, LocalDate date,
                                             String timeText, String reason) {
        String cleanName = name == null ? "" : name.trim();
        String cleanPhone = phone == null ? "" : phone.trim();
        String cleanReason = reason == null ? "" : reason.trim();
        if (cleanName.isEmpty()) throw new IllegalArgumentException("Please enter your full name.");
        if (cleanPhone.isEmpty()) throw new IllegalArgumentException("Please enter your phone number.");
        if (doctorOption == null || doctorOption.isBlank())
            throw new IllegalArgumentException("Please select a doctor.");
        if (date == null) throw new IllegalArgumentException("Please choose a preferred date.");
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Preferred date cannot be in the past.");
        if (timeText == null || timeText.isBlank())
            throw new IllegalArgumentException("Please select a preferred time.");
        if (cleanReason.isEmpty()) throw new IllegalArgumentException("Please write the reason for visit.");

        LocalTime time;
        try {
            time = LocalTime.parse(timeText.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Time must be HH:MM, e.g. 10:30.");
        }

        String doctorId = doctorOption.split(" — ")[0].trim();

        Patient patient = findByPhone(state, cleanPhone);
        if (patient == null) {
            int age = 0;
            if (ageText != null && !ageText.isBlank()) {
                try {
                    age = Integer.parseInt(ageText.trim());
                    if (age < 0 || age > 150) throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Age must be a number between 0 and 150.");
                }
            }
            String newId = state.patientService.nextPatientId();
            patient = new Patient(newId, cleanName, cleanPhone, "Other", "", cleanPhone, age, "");
            state.patientService.registerPatient(patient);
        }

        Appointment created = state.appointmentService.createAppointment(
                patient.getId(), doctorId, date, time, cleanReason);
        try {
            state.refreshAppointments();
        } catch (Exception ignored) {
        }
        try {
            state.refreshFromService();
        } catch (Exception ignored) {
        }
        return created;
    }

    private static Patient findByPhone(AppState state, String phone) {
        String digits = phone.replaceAll("\\D", "");
        List<Patient> all;
        try {
            all = state.patientService.getAllPatients();
        } catch (Exception ex) {
            all = state.patients;
        }
        if (all == null) return null;
        for (Patient p : all) {
            if (p.getPhone() == null) continue;
            String pDigits = p.getPhone().replaceAll("\\D", "");
            if (!pDigits.isEmpty() && (pDigits.equals(digits)
                    || (!digits.isEmpty() && pDigits.endsWith(digits))
                    || (!pDigits.isEmpty() && digits.endsWith(pDigits)))) {
                return p;
            }
        }
        return null;
    }
}
