package com.hospital.ui.views;

import com.hospital.model.EmergencyCase;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Comparator;

public class EmergencyView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("main-content");
        Label title = new Label("Emergency Department");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Triage queue and critical case management");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        Button add = new Button("+ New Emergency Case");
        add.getStyleClass().addAll("btn-primary");
        add.setOnAction(e -> showNewCaseDialog(state));
        HBox header = new HBox(12, headText, new Region(), add);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        HBox cols = new HBox(16);
        VBox criticalCard = buildCriticalCard(state);
        VBox queueCard = buildQueueCard(state);
        HBox.setHgrow(criticalCard, Priority.ALWAYS);
        HBox.setHgrow(queueCard, Priority.ALWAYS);
        criticalCard.setPrefWidth(420);
        queueCard.setPrefWidth(420);
        cols.getChildren().addAll(criticalCard, queueCard);
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button start = new Button("Start Treatment"); start.getStyleClass().addAll("btn-primary");
        Button complete = new Button("Complete"); complete.getStyleClass().addAll("btn-secondary");
        Button cancel = new Button("Cancel"); cancel.getStyleClass().addAll("btn-danger");
        Label hint = new Label("Tip: Next patient or enter case ID.");
        hint.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
        start.setOnAction(e -> { try { var c = state.emergencyService.startTreatment(); state.refreshEmergency(); info("Treatment started for " + resolveStatic(state,c) + " • " + c.getId()); } catch(Exception ex){ error(ex.getMessage()); } });
        complete.setOnAction(e -> promptId("Complete", "Enter case ID to complete", id -> { try { state.emergencyService.completeTreatment(id); state.refreshEmergency(); info("Completed " + id); } catch(Exception ex){ error(ex.getMessage()); } }));
        cancel.setOnAction(e -> promptId("Cancel", "Enter case ID to cancel", id -> { try { state.emergencyService.cancelEmergencyCase(id); state.refreshEmergency(); info("Cancelled " + id); } catch(Exception ex){ error(ex.getMessage()); } }));
        VBox actionsBox = new VBox(8, new HBox(8, start, complete, cancel), hint);
        ScrollPane sp = new ScrollPane(new VBox(16, header, cols, actionsBox));
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }
    private static VBox buildCriticalCard(AppState state) {
        VBox card = new VBox(0); card.getStyleClass().add("card");
        Label h = new Label("Critical Cases"); h.getStyleClass().add("section-title"); h.setPadding(new Insets(14,16,10,16));
        Separator sep = new Separator();
        VBox list = new VBox(0);
        var crit = state.emergencyCases.stream().filter(e -> "CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority())) && "WAITING".equals(e.getStatus().name())).sorted(Comparator.comparing(e -> e.getArrivalTime()!=null?e.getArrivalTime():java.time.LocalDateTime.MIN)).limit(5).toList();
        if (crit.isEmpty()) {
            list.getChildren().add(empty("No critical cases.", "Queue is clear — all stable."));
        } else {
            for (EmergencyCase ec : crit) {
                VBox row = new VBox(6);
                row.setPadding(new Insets(12,16,12,16));
                row.setStyle("-fx-border-color: #7F1D1D; -fx-border-width: 0 0 0 3; -fx-background-color: #1E293B;");
                Label name = new Label(resolveStatic(state, ec));
                name.setStyle("-fx-font-weight:600;-fx-font-size:13px;-fx-text-fill:#F1F5F9;");
                Label desc = new Label(ec.getDescription()!=null?ec.getDescription():"—");
                desc.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:12px;");
                Label meta = new Label(ec.getId()+"  •  "+(ec.getArrivalTime()!=null?ec.getArrivalTime().toLocalTime().toString().substring(0,5):"—"));
                meta.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
                HBox badges = new HBox(6, BadgeFactory.priority(String.valueOf(ec.getPriority())), BadgeFactory.status(ec.getStatus().name()));
                row.getChildren().addAll(name, desc, meta, badges);
                list.getChildren().add(row);
                list.getChildren().add(new Separator());
            }
        }
        card.getChildren().addAll(h, sep, list);
        return card;
    }
    private static VBox buildQueueCard(AppState state) {
        VBox card = new VBox(0); card.getStyleClass().add("card");
        Label h = new Label("Emergency Queue"); h.getStyleClass().add("section-title"); h.setPadding(new Insets(14,16,10,16));
        Separator sep = new Separator();
        HBox th = new HBox(8);
        th.setPadding(new Insets(9,16,8,16));
        th.setStyle("-fx-background-color:#1E1B4B;");
        th.getChildren().addAll(hdr(" #", 24), hdr("PATIENT", 110), hdr("PRIORITY", 88), hdr("ID", 64));
        VBox list = new VBox(0);
        var queue = state.emergencyService.viewEmergencyQueue();
        if (queue.isEmpty()) {
            list.getChildren().add(empty("No emergency cases found.", "All emergency cases are currently resolved."));
        } else {
            for (int i=0;i<Math.min(8, queue.size());i++) {
                EmergencyCase ec = queue.get(i);
                HBox row = new HBox(8);
                row.setPadding(new Insets(10,16,10,16));
                row.setAlignment(Pos.CENTER_LEFT);
                if (i%2==1) row.setStyle("-fx-background-color:#1E1B4B;");
                Label idx = new Label(String.valueOf(i+1));
                idx.setStyle("-fx-font-weight:700;-fx-text-fill:#64748B;-fx-font-size:12px;"); idx.setPrefWidth(24);
                Label name = new Label(resolveStatic(state, ec)); name.setPrefWidth(110); name.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-text-fill:#E2E8F0;");
                Label prio = BadgeFactory.priority(String.valueOf(ec.getPriority())); prio.setPrefWidth(82);
                Label id = new Label(ec.getId()); id.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;"); id.setPrefWidth(64);
                row.getChildren().addAll(idx, name, prio, id);
                list.getChildren().add(row);
                if (i < Math.min(8, queue.size())-1) list.getChildren().add(sepThin());
            }
        }
        card.getChildren().addAll(h, sep, th, list);
        return card;
    }
    private static String resolveStatic(AppState state, EmergencyCase ec){ return state.patients.stream().filter(p->p.getId().equals(ec.getPatientId())).map(p->p.getName()).findFirst().orElse(ec.getPatientId()); }
    private static Label hdr(String t,double w){ Label l=new Label(t); l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#818CF8;-fx-letter-spacing:0.6px;"); l.setPrefWidth(w); return l; }
    private static VBox empty(String a,String b){ VBox box=new VBox(4); box.setAlignment(Pos.CENTER); box.setPadding(new Insets(28,16,28,16)); Label t=new Label(a); t.getStyleClass().add("empty-state-title"); Label s=new Label(b); s.getStyleClass().add("empty-state-sub"); s.setWrapText(true); box.getChildren().addAll(t,s); return box; }
    private static Separator sepThin(){ Separator s=new Separator(); s.setOpacity(0.45); return s; }
    private static void showNewCaseDialog(AppState state){
        Dialog<Void> d=new Dialog<>(); d.setTitle("New Emergency Case"); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ComboBox<String> pat=new ComboBox<>(FXCollections.observableArrayList(state.patients.stream().map(p->p.getId()+" — "+p.getName()).toList())); pat.setPromptText("Select Patient"); pat.setPrefWidth(300);
        ComboBox<String> pri=new ComboBox<>(FXCollections.observableArrayList("CRITICAL","HIGH","MEDIUM","LOW")); pri.setPromptText("Priority"); pri.setPrefWidth(300);
        TextField desc=new TextField(); desc.setPromptText("Chest pain, accident, etc."); desc.setPrefWidth(300);
        VBox box=new VBox(8, new Label("Patient"), pat, new Label("Priority"), pri, new Label("Description"), desc); box.setPadding(new Insets(16)); box.setSpacing(6);
        d.getDialogPane().setContent(box);
        d.setResultConverter(b->{ if(b==ButtonType.OK){ try{ String pid=pat.getValue()!=null?pat.getValue().split(" — ")[0].trim():null; String pr=pri.getValue(); if(pid==null||pr==null||desc.getText().isBlank()) throw new IllegalArgumentException("Patient, priority and description are required"); state.emergencyService.addEmergencyCase(pid, com.hospital.model.EmergencyPriority.valueOf(pr), desc.getText().trim()); state.refreshEmergency(); }catch(Exception ex){ error(ex.getMessage()); } } return null; });
        d.showAndWait();
    }
    private static void promptId(String title,String header, java.util.function.Consumer<String> fn){ TextInputDialog d=new TextInputDialog(); d.setTitle(title); d.setHeaderText(header); var r=d.showAndWait(); r.ifPresent(v -> { if(!v.isBlank()) fn.accept(v.trim()); }); }
    private static void info(String m){ Alert a=new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
    private static void error(String m){ Alert a=new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
}
