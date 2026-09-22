package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class AppointmentsView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Appointments");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Schedule and track patient consultations");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        TextField search = new TextField(); search.setPromptText("Search"); search.setPrefWidth(170); search.getStyleClass().add("header-search");
        DatePicker dateFilter = new DatePicker(); dateFilter.setPromptText("Date"); dateFilter.setPrefWidth(138);
        ComboBox<String> doctorFilter = new ComboBox<>(FXCollections.observableArrayList(state.doctors.stream().map(d-> d.getId()+" — "+d.getName()).toList()));
        doctorFilter.setPromptText("Doctor"); doctorFilter.setPrefWidth(160);
        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("ALL","SCHEDULED","CONFIRMED","COMPLETED","CANCELLED"));
        statusFilter.setValue("ALL"); statusFilter.setPrefWidth(132);
        Button newBtn = new Button("+ New Appointment"); newBtn.getStyleClass().addAll("btn-primary");
        HBox filters = new HBox(8, search, dateFilter, doctorFilter, statusFilter);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox header = new HBox(12, headText, new Region(), filters, newBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<Appointment,String> cDate = col("Date", 94, a -> a.getDate()!=null?a.getDate().toString():"—");
        TableColumn<Appointment,String> cTime = col("Time", 68, a -> a.getFormattedTime());
        TableColumn<Appointment,String> cPat = col("Patient", 122, a -> a.getPatientName());
        TableColumn<Appointment,String> cDoc = col("Doctor", 122, a -> a.getDoctorName());
        TableColumn<Appointment,String> cReason = col("Reason", 150, a -> a.getReason()!=null?a.getReason():"—");
        TableColumn<Appointment,String> cStatus = new TableColumn<>("Status"); cStatus.setPrefWidth(102);
        cStatus.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String it, boolean emp){
                super.updateItem(it,emp);
                if(emp || getTableRow()==null || getTableRow().getItem()==null){ setGraphic(null); setText(null); return; }
                Appointment a=(Appointment)getTableRow().getItem();
                setGraphic(BadgeFactory.status(a.getStatus()!=null?a.getStatus().name():""));
                setText(null);
            }
        });
        TableColumn<Appointment,Void> cAct = new TableColumn<>("Actions"); cAct.setPrefWidth(96);
        cAct.setCellFactory(col -> new TableCell<>() {
            private final Button view=new Button("View");
            { view.getStyleClass().addAll("btn-ghost","btn-small"); view.setOnAction(e-> show(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void it, boolean emp){ super.updateItem(it,emp); setGraphic(emp?null:new HBox(view)); }
        });
        FilteredList<Appointment> filtered = new FilteredList<>(state.appointments, p->true);
        Runnable refilter = () -> filtered.setPredicate(a->{
            String q=search.getText();
            if(q!=null && !q.isBlank() && !a.getPatientName().toLowerCase().contains(q.toLowerCase()) && !a.getDoctorName().toLowerCase().contains(q.toLowerCase()) && !a.getReason().toLowerCase().contains(q.toLowerCase())) return false;
            if(dateFilter.getValue()!=null && !dateFilter.getValue().equals(a.getDate())) return false;
            String df=doctorFilter.getValue();
            if(df!=null && !a.getDoctorId().equals(df.split(" — ")[0])) return false;
            String st=statusFilter.getValue();
            if(st!=null && !"ALL".equals(st) && !st.equalsIgnoreCase(a.getStatus()!=null?a.getStatus().name():"")) return false;
            return true;
        });
        search.textProperty().addListener((o,ov,nv)-> refilter.run());
        dateFilter.valueProperty().addListener((o,ov,nv)-> refilter.run());
        doctorFilter.valueProperty().addListener((o,ov,nv)-> refilter.run());
        statusFilter.valueProperty().addListener((o,ov,nv)-> refilter.run());
        table.setItems(filtered);
        table.getColumns().addAll(cDate,cTime,cPat,cDoc,cReason,cStatus,cAct);
        table.setPlaceholder(new Label("No appointments found."));
        HBox quick = new HBox(8);
        quick.setAlignment(Pos.CENTER_LEFT);
        Button confirm = new Button("Confirm"); confirm.getStyleClass().addAll("btn-secondary","btn-small");
        Button complete = new Button("Complete"); complete.getStyleClass().addAll("btn-secondary","btn-small");
        Button cancel = new Button("Cancel"); cancel.getStyleClass().addAll("btn-danger","btn-small");
        Label hint = new Label("Select a row to change status, or reschedule via View.");
        hint.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
        confirm.setOnAction(e-> changeStatus(table, state, "CONFIRMED"));
        complete.setOnAction(e-> changeStatus(table, state, "COMPLETED"));
        cancel.setOnAction(e-> changeStatus(table, state, "CANCELLED"));
        Button resched = new Button("Reschedule"); resched.getStyleClass().addAll("btn-secondary","btn-small");
        resched.setOnAction(e-> rescheduleDialog(table, state));
        quick.getChildren().addAll(resched, confirm, complete, cancel, new Region(), hint);
        HBox.setHgrow(quick.getChildren().get(5), Priority.ALWAYS);
        newBtn.setOnAction(e-> { showCreateDialog(state); });
        VBox.setVgrow(table, Priority.ALWAYS);
        root.getChildren().addAll(header, table, quick);
        return root;
    }
    private static TableColumn<Appointment,String> col(String n,double w, java.util.function.Function<Appointment,String> fn){
        TableColumn<Appointment,String> c=new TableColumn<>(n);
        c.setCellValueFactory(d-> new javafx.beans.property.SimpleStringProperty(fn.apply(d.getValue())!=null?fn.apply(d.getValue()):"—"));
        c.setPrefWidth(w); return c;
    }
    private static void changeStatus(TableView<Appointment> table, AppState state, String target){
        Appointment a=table.getSelectionModel().getSelectedItem();
        if(a==null){ alert("Select an appointment first."); return; }
        try{
            switch(target){
                case "CONFIRMED" -> state.appointmentService.confirmAppointment(a.getId());
                case "COMPLETED" -> state.appointmentService.completeAppointment(a.getId());
                case "CANCELLED" -> state.appointmentService.cancelAppointment(a.getId());
            }
            state.refreshAppointments();
            alert(target+" ✓  "+a.getId());
        }catch(Exception ex){ error(ex.getMessage()); }
    }
    private static void rescheduleDialog(TableView<Appointment> table, AppState state){
        Appointment a=table.getSelectionModel().getSelectedItem();
        if(a==null){ alert("Select an appointment to reschedule."); return; }
        Dialog<Void> d=new Dialog<>(); d.setTitle("Reschedule "+a.getId()); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        DatePicker date=new DatePicker(a.getDate()!=null?a.getDate():LocalDate.now());
        TextField time=new TextField(a.getTime()!=null?a.getTime().toString().substring(0,5):"10:30");
        time.setPromptText("HH:MM");
        VBox box=new VBox(8,new Label("New Date"),date,new Label("New Time"),time); box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b->{ if(b==ButtonType.OK){ try{ java.time.LocalTime t=java.time.LocalTime.parse(time.getText().trim()); state.appointmentService.rescheduleAppointment(a.getId(), date.getValue(), t); state.refreshAppointments(); alert("Rescheduled ✓"); }catch(Exception ex){ error(ex.getMessage()); } } return null; });
        d.showAndWait();
    }
    private static void showCreateDialog(AppState state){
        Dialog<Void> d=new Dialog<>(); d.setTitle("Create Appointment"); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ComboBox<String> pat=new ComboBox<>(FXCollections.observableArrayList(state.patients.stream().map(p->p.getId()+" — "+p.getName()).toList())); pat.setPromptText("Select Patient"); pat.setPrefWidth(280);
        ComboBox<String> doc=new ComboBox<>(FXCollections.observableArrayList(state.doctors.stream().map(x->x.getId()+" — "+x.getName()).toList())); doc.setPromptText("Select Doctor"); doc.setPrefWidth(280);
        DatePicker date=new DatePicker(LocalDate.now()); date.setPrefWidth(280);
        TextField time=new TextField(); time.setPromptText("HH:MM e.g. 10:30"); time.setPrefWidth(280);
        TextField reason=new TextField(); reason.setPromptText("Reason"); reason.setPrefWidth(280);
        VBox box=new VBox(6,new Label("Patient"),pat,new Label("Doctor"),doc,new Label("Date"),date,new Label("Time"),time,new Label("Reason"),reason); box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b->{ if(b==ButtonType.OK){ try{ String pid=pat.getValue()!=null?pat.getValue().split(" — ")[0]:null; String did=doc.getValue()!=null?doc.getValue().split(" — ")[0]:null; if(pid==null||did==null||date.getValue()==null||time.getText().isBlank()||reason.getText().isBlank()) throw new IllegalArgumentException("All fields are required"); java.time.LocalTime t=java.time.LocalTime.parse(time.getText().trim()); state.appointmentService.createAppointment(pid,did,date.getValue(),t,reason.getText().trim()); state.refreshAppointments(); Alert ok=new Alert(Alert.AlertType.INFORMATION); ok.setHeaderText(null); ok.setContentText("Appointment created successfully."); ok.showAndWait(); }catch(Exception ex){ Alert er=new Alert(Alert.AlertType.ERROR); er.setHeaderText(null); er.setContentText(ex.getMessage()); er.showAndWait();} } return null; });
        d.showAndWait();
    }
    private static void show(Appointment a){ Alert al=new Alert(Alert.AlertType.INFORMATION); al.setTitle("Appointment "+a.getId()); al.setHeaderText(a.getPatientName()+" → "+a.getDoctorName()); al.setContentText("Date: "+a.getDate()+"\nTime: "+a.getFormattedTime()+"\nStatus: "+a.getStatus()+"\nReason: "+a.getReason()+"\nCreated: "+(a.getCreatedAt()!=null?a.getCreatedAt().toString().substring(0,16):"—")); al.showAndWait(); }
    private static void alert(String m){ Alert a=new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
    private static void error(String m){ Alert a=new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
}
