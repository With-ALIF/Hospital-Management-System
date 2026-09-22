package com.hospital.ui.views;

import com.hospital.model.Patient;
import com.hospital.ui.AppState;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PatientsView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Patients");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Manage patient records and medical profiles");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        TextField search = new TextField();
        search.setPromptText("Search patient...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(260);
        Button add = new Button("+ Add Patient");
        add.getStyleClass().addAll("btn-primary");
        HBox header = new HBox(12, headText, new Region(), search, add);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<Patient,String> cId = col("Patient ID", 88, p -> p.getId());
        TableColumn<Patient,String> cName = col("Name", 150, p -> p.getName());
        TableColumn<Patient,String> cGender = col("Gender", 80, p -> p.getGender());
        TableColumn<Patient,String> cBlood = col("Blood Group", 92, p -> p.getBloodGroup());
        TableColumn<Patient,String> cPhone = col("Phone", 128, p -> p.getPhone());
        TableColumn<Patient,String> cStatus = new TableColumn<>("Status");
        cStatus.setPrefWidth(84);
        cStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("Active"));
        cStatus.setCellFactory(tc -> new TableCell<>() {
            private final Label badge = new Label("Active");
            { badge.getStyleClass().addAll("badge","badge-success"); }
            @Override protected void updateItem(String it, boolean emp) { super.updateItem(it, emp); setGraphic(emp ? null : badge); setText(null); }
        });
        TableColumn<Patient,Void> cAct = new TableColumn<>("Actions");
        cAct.setPrefWidth(130);
        cAct.setCellFactory(col -> new TableCell<>() {
            private final Button view = new Button("View");
            private final Button edit = new Button("Edit");
            { view.getStyleClass().addAll("btn-ghost","btn-small"); edit.getStyleClass().addAll("btn-ghost","btn-small"); view.setOnAction(e -> showDetail(state, getTableView().getItems().get(getIndex()))); edit.setOnAction(e -> showDetail(state, getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void it, boolean emp) { super.updateItem(it, emp); setGraphic(emp ? null : new HBox(6, view, edit)); }
        });
        FilteredList<Patient> filtered = new FilteredList<>(state.patients, p -> true);
        search.textProperty().addListener((obs,o,n) -> filtered.setPredicate(p -> n==null || n.isBlank() || p.getName().toLowerCase().contains(n.toLowerCase()) || p.getId().toLowerCase().contains(n.toLowerCase()) || (p.getPhone()!=null && p.getPhone().contains(n)) || (p.getBloodGroup()!=null && p.getBloodGroup().toLowerCase().contains(n.toLowerCase()))));
        table.setItems(filtered);
        table.getColumns().addAll(cId,cName,cGender,cBlood,cPhone,cStatus,cAct);
        table.setPlaceholder(new Label("No patients found."));
        VBox.setVgrow(table, Priority.ALWAYS);
        add.setOnAction(e -> { showAddDialog(state); state.refreshFromService(); });
        root.getChildren().addAll(header, table);
        return root;
    }
    private static TableColumn<Patient,String> col(String name, double w, java.util.function.Function<Patient,String> fn){
        TableColumn<Patient,String> c = new TableColumn<>(name);
        c.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(fn.apply(d.getValue())!=null?fn.apply(d.getValue()):"—"));
        c.setPrefWidth(w); return c;
    }
    private static void showAddDialog(AppState state){
        Dialog<Void> d = new Dialog<>(); d.setTitle("Add Patient"); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField name = new TextField(); name.setPromptText("Full name");
        TextField phone = new TextField(); phone.setPromptText("01700000000");
        ComboBox<String> gender = new ComboBox<>(); gender.getItems().addAll("Male","Female","Other"); gender.setValue("Male");
        TextField blood = new TextField(); blood.setPromptText("O+");
        TextField emergency = new TextField(); emergency.setPromptText("Emergency contact");
        VBox box = new VBox(8, new Label("Name"), name, new Label("Phone"), phone, new Label("Gender"), gender, new Label("Blood Group"), blood, new Label("Emergency Contact"), emergency);
        box.setPadding(new Insets(16)); d.getDialogPane().setContent(box);
        d.setResultConverter(b -> { if (b==ButtonType.OK){ try{ if(name.getText().isBlank()||phone.getText().isBlank()) throw new IllegalArgumentException("Name and phone required"); String id = state.patientService.nextPatientId(); Patient p = new Patient(id, name.getText().trim(), phone.getText().trim(), gender.getValue(), blood.getText().trim(), emergency.getText().trim()); state.patientService.registerPatient(p); state.refreshFromService(); }catch(Exception ex){ Alert a=new Alert(Alert.AlertType.ERROR); a.setContentText(ex.getMessage()); a.showAndWait();} } return null; });
        d.showAndWait();
    }
    private static void showDetail(AppState state, Patient p){
        Dialog<Void> d = new Dialog<>(); d.setTitle("Patient Profile"); d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); d.getDialogPane().setPrefWidth(520);
        Label name = new Label(p.getName()); name.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-text-fill:#F1F5F9;");
        Label pid = new Label("Patient ID: "+p.getId()); pid.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;");
        VBox head = new VBox(2, name, pid); head.setPadding(new Insets(16,16,12,16)); head.setStyle("-fx-background-color:#1E293B;-fx-border-color:#334155;-fx-border-width:0 0 1 0;");
        GridPane grid = new GridPane(); grid.setHgap(24); grid.setVgap(8); grid.setPadding(new Insets(16));
        addInfo(grid,0,0,"Gender", p.getGender()); addInfo(grid,1,0,"Blood Group", p.getBloodGroup()); addInfo(grid,0,1,"Phone", p.getPhone()); addInfo(grid,1,1,"Emergency", p.getEmergencyContact());
        VBox infoCard = new VBox(grid); infoCard.getStyleClass().add("card"); infoCard.setPadding(new Insets(0));
        TabPane tabs = new TabPane(); tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab t1 = new Tab("Appointments", buildAppointmentsTab(state, p)); Tab t2 = new Tab("Emergency Cases", buildEmergencyTab(state, p)); Tab t3 = new Tab("Treatment History", buildHistoryTab(state, p));
        tabs.getTabs().addAll(t1,t2,t3);
        VBox content = new VBox(head, infoCard, tabs); content.setSpacing(12);
        ScrollPane sp = new ScrollPane(content); sp.setFitToWidth(true); sp.setPrefHeight(420); d.getDialogPane().setContent(sp); d.showAndWait();
    }
    private static void addInfo(GridPane g,int col,int row,String label,String value){
        Label l = new Label(label.toUpperCase()); l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#94A3B8;-fx-letter-spacing:0.5px;");
        Label v = new Label(value!=null && !value.isBlank()?value:"—"); v.setStyle("-fx-font-size:13px;-fx-text-fill:#F1F5F9;-fx-font-weight:500;");
        VBox box = new VBox(2,l,v); g.add(box,col,row);
    }
    private static VBox buildAppointmentsTab(AppState state, Patient p){
        var list = state.appointmentService.getAppointmentsByPatient(p.getId());
        if(list.isEmpty()) return emptyBox("No appointments", "This patient has no scheduled appointments.");
        VBox box = new VBox(0);
        for(var a: list){ HBox row = new HBox(12); row.setPadding(new Insets(10,12,10,12)); row.setAlignment(Pos.CENTER_LEFT); Label d = new Label(a.getDate()!=null?a.getDate().toString():"—"); d.setPrefWidth(90); d.setStyle("-fx-font-size:12px;-fx-text-fill:#E2E8F0;"); Label t = new Label(a.getFormattedTime()); t.setPrefWidth(60); t.setStyle("-fx-font-weight:600;-fx-text-fill:#F1F5F9;"); Label doc = new Label(a.getDoctorName()); doc.setPrefWidth(110); doc.setStyle("-fx-text-fill:#94A3B8;"); Label st = new Label(a.getStatus()!=null?a.getStatus().name():""); st.getStyleClass().addAll("badge", statusStyle(a.getStatus()!=null?a.getStatus().name():"")); row.getChildren().addAll(d,t,doc,st); box.getChildren().add(row); box.getChildren().add(new Separator()); }
        return box;
    }
    private static VBox buildEmergencyTab(AppState state, Patient p){
        var list = state.emergencyService.getCasesForPatient(p.getId());
        if(list.isEmpty()) return emptyBox("No emergency cases", "No emergency records for this patient.");
        VBox box = new VBox(0);
        for(var e: list){ HBox row = new HBox(12); row.setPadding(new Insets(10,12,10,12)); Label pr = new Label(String.valueOf(e.getPriority())); pr.getStyleClass().addAll("badge", prioStyle(String.valueOf(e.getPriority()))); Label st = new Label(e.getStatus().name()); st.getStyleClass().addAll("badge", statusStyle(e.getStatus().name())); Label desc = new Label(e.getDescription()!=null?e.getDescription():"—"); desc.setStyle("-fx-text-fill:#CBD5E1;"); row.getChildren().addAll(pr, st, desc); box.getChildren().add(row); box.getChildren().add(new Separator()); }
        return box;
    }
    private static Pane buildHistoryTab(AppState state, Patient p){ VBox box = new VBox(6); box.setPadding(new Insets(12)); box.getChildren().add(new Label("Treatment history aggregates appointments and emergency cases for "+p.getName()){{setStyle("-fx-text-fill:#94A3B8;");}}); return box; }
    private static VBox emptyBox(String t,String s){ VBox b=new VBox(6); b.setAlignment(Pos.CENTER); b.setPadding(new Insets(24)); Label a=new Label(t); a.getStyleClass().add("empty-state-title"); Label b2=new Label(s); b2.getStyleClass().add("empty-state-sub"); b.getChildren().addAll(a,b2); return b; }
    private static String statusStyle(String s){ if(s==null) return "badge-neutral"; switch(s.toUpperCase()){ case "SCHEDULED": return "badge-info"; case "CONFIRMED": return "badge-success"; case "COMPLETED": return "badge-success"; case "CANCELLED": return "badge-danger"; case "WAITING": return "badge-warning"; case "IN_TREATMENT": return "badge-info"; default: return "badge-neutral"; } }
    private static String prioStyle(String p){ if(p==null) return "badge-neutral"; switch(p.toUpperCase()){ case "CRITICAL": return "badge-critical"; case "HIGH": return "badge-high"; case "MEDIUM": return "badge-medium"; case "LOW": return "badge-low"; default: return "badge-neutral"; } }
}
