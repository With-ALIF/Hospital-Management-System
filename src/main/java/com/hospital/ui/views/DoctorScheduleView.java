package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;

public class DoctorScheduleView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Doctor Schedule");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Availability and daily timeline — conflicts highlighted");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        ComboBox<String> docSel = new ComboBox<>(FXCollections.observableArrayList(state.doctors.stream().map(d-> d.getId()+" — "+d.getName()+" ("+d.getSpecialization()+")").toList()));
        docSel.setPromptText("Select doctor");
        docSel.setPrefWidth(280);
        if (!state.doctors.isEmpty()) docSel.setValue(state.doctors.get(0).getId()+" — "+state.doctors.get(0).getName()+" ("+state.doctors.get(0).getSpecialization()+")");
        HBox top = new HBox(12, headText, new Region(), docSel);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        VBox timeline = new VBox(0);
        timeline.getStyleClass().add("card");
        ScrollPane sp = new ScrollPane(timeline);
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        Runnable refresh = () -> {
            timeline.getChildren().clear();
            String sel = docSel.getValue();
            if (sel==null) { timeline.getChildren().add(empty("Select a doctor","Choose a doctor to view schedule.")); return; }
            String did = sel.split(" — ")[0].trim();
            Doctor doc = state.doctors.stream().filter(d->d.getId().equals(did)).findFirst().orElse(null);
            if (doc==null) return;
            HBox docHead = new HBox(12);
            docHead.setPadding(new Insets(14,16,12,16));
            docHead.setAlignment(Pos.CENTER_LEFT);
            Label name = new Label(doc.getName());
            name.setStyle("-fx-font-weight:700;-fx-font-size:13px;-fx-text-fill:#F1F5F9;");
            Label spec = new Label(doc.getSpecialization());
            spec.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:11px;-fx-background-color:#312E81;-fx-padding:2 6;-fx-background-radius:4;");
            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
            Label avail = new Label(doc.getAvailable()? "Available" : "On Leave");
            avail.getStyleClass().addAll("badge", doc.getAvailable()? "badge-success":"badge-neutral");
            docHead.getChildren().addAll(name, spec, s, avail);
            timeline.getChildren().add(docHead);
            timeline.getChildren().add(new Separator());
            Label day = new Label("Today  •  "+java.time.LocalDate.now().toString());
            day.setStyle("-fx-font-weight:600;-fx-text-fill:#E2E8F0;-fx-padding:10 16 6 16; -fx-font-size:12px;");
            timeline.getChildren().add(day);
            HBox legend = new HBox(12);
            legend.setPadding(new Insets(0,16,8,16));
            legend.getChildren().addAll(legendDot("#22C55E","Available"), legendDot("#6366F1","Appointment"), legendDot("#EF4444","Conflict / Unavailable"));
            timeline.getChildren().add(legend);
            timeline.getChildren().add(new Separator());
            var appts = state.appointments.stream().filter(a-> did.equals(a.getDoctorId()) && java.time.LocalDate.now().equals(a.getDate())).sorted((a,b)-> a.getTime().compareTo(b.getTime())).toList();
            for (int hour=8; hour<=18; hour++) {
                for (int half=0; half<2; half++) {
                    if (hour==18 && half==1) break;
                    LocalTime t = LocalTime.of(hour, half*30);
                    HBox row = new HBox(12);
                    row.setPadding(new Insets(9,16,9,16));
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label time = new Label(String.format("%02d:%02d", hour, half*30));
                    time.setPrefWidth(56); time.setStyle("-fx-font-weight:600;-fx-font-size:11.5px;-fx-font-variant-numeric: tabular-nums; -fx-text-fill:#CBD5E1;");
                    var apptList = appts.stream().filter(a-> a.getTime()!=null && a.getTime().equals(t)).toList();
                    if (apptList.size()>1) {
                        row.setStyle("-fx-background-color:rgba(239,68,68,0.15); -fx-border-color:#7F1D1D; -fx-border-width:0 0 0 3;");
                        Label info = new Label("Conflict — "+apptList.size()+" appointments at "+time.getText());
                        info.setStyle("-fx-text-fill:#FCA5A5;-fx-font-weight:600;-fx-font-size:11.5px;");
                        row.getChildren().addAll(time, info);
                    } else if (!apptList.isEmpty()) {
                        Appointment a = apptList.get(0);
                        boolean cancelled = a.getStatus()!=null && "CANCELLED".equals(a.getStatus().name());
                        row.setStyle(cancelled ? "-fx-background-color:#1E1B4B; -fx-opacity:0.7;" : "-fx-background-color:rgba(99,102,241,0.18); -fx-border-color:#4338CA; -fx-border-width:0 0 0 3;");
                        Label pat = new Label(a.getPatientName()); pat.setStyle("-fx-font-weight:500;-fx-font-size:12px;-fx-text-fill:#F1F5F9;");
                        Label reason = new Label(a.getReason()!=null?a.getReason():""); reason.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:11px;");
                        Label st = new Label(a.getStatus()!=null?a.getStatus().name():""); st.getStyleClass().addAll("badge", badgeFor(a.getStatus()!=null?a.getStatus().name():""));
                        Region sp2=new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
                        row.getChildren().addAll(time, pat, reason, sp2, st);
                    } else {
                        boolean availSlot = doc.isAvailableAt(t) && doc.getAvailable();
                        Label st = new Label(availSlot? "Available" : "Unavailable");
                        st.getStyleClass().addAll("badge", availSlot? "badge-success":"badge-neutral");
                        Region sp2=new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
                        row.getChildren().addAll(time, sp2, st);
                        if (!availSlot) row.setOpacity(0.55);
                    }
                    timeline.getChildren().add(row);
                    Separator sep=new Separator(); sep.setOpacity(0.4);
                    timeline.getChildren().add(sep);
                }
            }
        };
        docSel.valueProperty().addListener((o,ov,nv)-> refresh.run());
        refresh.run();
        root.getChildren().addAll(top, sp);
        return root;
    }
    private static HBox legendDot(String color, String label){
        Region dot=new Region(); dot.setStyle("-fx-background-color:"+color+";-fx-min-width:8;-fx-min-height:8;-fx-max-width:8;-fx-max-height:8;-fx-background-radius:4;");
        Label l=new Label(label); l.setStyle("-fx-font-size:11px;-fx-text-fill:#94A3B8;");
        HBox b=new HBox(6, dot, l); b.setAlignment(Pos.CENTER_LEFT);
        return b;
    }
    private static String badgeFor(String s){
        if(s==null) return "badge-neutral";
        switch(s.toUpperCase()){
            case "SCHEDULED": return "badge-info";
            case "CONFIRMED": return "badge-success";
            case "COMPLETED": return "badge-success";
            case "CANCELLED": return "badge-danger";
            default: return "badge-neutral";
        }
    }
    private static VBox empty(String a,String b){
        VBox box=new VBox(4); box.setAlignment(Pos.CENTER); box.setPadding(new Insets(28));
        Label t=new Label(a); t.getStyleClass().add("empty-state-title");
        Label s=new Label(b); s.getStyleClass().add("empty-state-sub");
        box.getChildren().addAll(t,s); return box;
    }
}
