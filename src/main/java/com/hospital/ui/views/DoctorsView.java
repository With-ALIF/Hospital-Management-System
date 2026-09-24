package com.hospital.ui.views;

import com.hospital.model.Doctor;
import com.hospital.ui.AppState;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DoctorsView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Doctors");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Staff directory and availability");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        TextField search = new TextField();
        search.setPromptText("Search doctor...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(260);
        Button add = new Button("+ Add Doctor");
        add.getStyleClass().addAll("btn-primary");
        HBox header = new HBox(12, headText, new Region(), search, add);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        TableView<Doctor> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<Doctor,String> cName = col("Doctor", 170, d -> d.getName());
        TableColumn<Doctor,String> cSpec = col("Specialization", 140, d -> d.getSpecialization());
        TableColumn<Doctor,String> cAvail = new TableColumn<>("Availability");
        cAvail.setPrefWidth(118);
        cAvail.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAvailable() ? "Available" : "Busy"));
        cAvail.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String it, boolean emp){
                super.updateItem(it, emp);
                if(emp || getTableRow()==null || getTableRow().getItem()==null){ setGraphic(null); setText(null); return; }
                Doctor d = (Doctor) getTableRow().getItem();
                HBox box = new HBox(6);
                box.setAlignment(Pos.CENTER_LEFT);
                Region dot = new Region();
                dot.getStyleClass().add(d.getAvailable() ? "status-dot-available" : "status-dot-busy");
                Label txt = new Label(d.getAvailable() ? "Available" : "Busy");
                txt.setStyle("-fx-font-size:11.5px;-fx-font-weight:600;-fx-text-fill:"+(d.getAvailable()?"#4ADE80":"#FBBF24")+";");
                box.getChildren().addAll(dot, txt);
                setGraphic(box);
                setText(null);
            }
        });
        TableColumn<Doctor,String> cSched = col("Today's Schedule", 190, d -> d.getDutyScheduleString());
        TableColumn<Doctor,Void> cAct = new TableColumn<>("Actions");
        cAct.setPrefWidth(120);
        cAct.setCellFactory(col -> new TableCell<>() {
            private final Button view = new Button("View");
            { view.getStyleClass().addAll("btn-ghost","btn-small"); view.setOnAction(e -> show(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void it, boolean emp){ super.updateItem(it,emp); setGraphic(emp?null:new HBox(view));}
        });
        FilteredList<Doctor> filtered = new FilteredList<>(state.doctors, p->true);
        search.textProperty().addListener((o,ov,nv)-> filtered.setPredicate(d-> nv==null||nv.isBlank()|| d.getName().toLowerCase().contains(nv.toLowerCase())|| d.getSpecialization().toLowerCase().contains(nv.toLowerCase())|| d.getId().toLowerCase().contains(nv.toLowerCase())));
        table.setItems(filtered);
        table.getColumns().addAll(cName,cSpec,cAvail,cSched,cAct);
        table.setPlaceholder(new Label("No doctors found."));
        VBox.setVgrow(table, Priority.ALWAYS);
        add.setOnAction(e -> { showAddDialog(state); state.refreshFromService(); });
        root.getChildren().addAll(header, table);
        return root;
    }
    private static TableColumn<Doctor,String> col(String n,double w, java.util.function.Function<Doctor,String> fn){
        TableColumn<Doctor,String> c=new TableColumn<>(n);
        c.setCellValueFactory(d-> new javafx.beans.property.SimpleStringProperty(fn.apply(d.getValue())!=null?fn.apply(d.getValue()):"—"));
        c.setPrefWidth(w); return c;
    }
    private static void showAddDialog(AppState state){
        Dialog<Void> d=new Dialog<>(); d.setTitle("Add Doctor"); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField name=new TextField(); name.setPromptText("Dr. Name");
        TextField phone=new TextField(); phone.setPromptText("Phone");
        ComboBox<String> gender=new ComboBox<>(); gender.getItems().addAll("Male","Female","Other"); gender.setValue("Male");
        TextField spec=new TextField(); spec.setPromptText("Cardiology");
        TextField slot1=new TextField(); slot1.setPromptText("09:00-13:00");
        TextField slot2=new TextField(); slot2.setPromptText("15:00-18:00 (optional)");
        VBox box=new VBox(8,new Label("Name"),name,new Label("Phone"),phone,new Label("Gender"),gender,new Label("Specialization"),spec,new Label("Duty Slot 1"),slot1,new Label("Duty Slot 2"),slot2);
        box.setPadding(new Insets(16)); d.getDialogPane().setContent(box);
        d.setResultConverter(b->{ if(b==ButtonType.OK){ try{ if(name.getText().isBlank()||spec.getText().isBlank()) throw new IllegalArgumentException("Name and specialization required"); String id=state.doctorService.nextDoctorId(); Doctor doc=new Doctor(id,name.getText().trim(),phone.getText().trim(),gender.getValue(),spec.getText().trim()); if(!slot1.getText().isBlank()) com.hospital.ui.SlotParser.parseAndAdd(doc, slot1.getText().trim()); if(!slot2.getText().isBlank()) com.hospital.ui.SlotParser.parseAndAdd(doc, slot2.getText().trim()); state.doctorService.registerDoctor(doc); state.refreshFromService(); }catch(Exception ex){ Alert a=new Alert(Alert.AlertType.ERROR); a.setContentText(ex.getMessage()); a.showAndWait();} } return null; });
        d.showAndWait();
    }
    private static void show(Doctor d){
        Dialog<Void> dlg=new Dialog<>(); dlg.setTitle("Doctor — "+d.getName()); dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        VBox box=new VBox(12); box.setPadding(new Insets(16));
        Label n=new Label(d.getName()); n.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#10263B;");
        Label s=new Label(d.getSpecialization()+" • "+d.getId()); s.setStyle("-fx-text-fill:#334155;");
        HBox avail=new HBox(6); Region dot=new Region(); dot.getStyleClass().add(d.getAvailable()?"status-dot-available":"status-dot-busy"); Label av=new Label(d.getAvailable()?"Available":"Busy"); av.setStyle("-fx-font-weight:600;-fx-text-fill:"+(d.getAvailable()?"#15803D":"#B45309")+";"); avail.getChildren().addAll(dot,av); avail.setAlignment(Pos.CENTER_LEFT);
        Label sched=new Label("Schedule: "+d.getDutyScheduleString()); sched.setStyle("-fx-text-fill:#10263B;");
        box.getChildren().addAll(n,s,avail, new Separator(), sched); dlg.getDialogPane().setContent(box); dlg.showAndWait();
    }
}
