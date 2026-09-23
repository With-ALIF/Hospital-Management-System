package com.hospital.ui.views;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.StaffRole;
import com.hospital.model.UserAccount;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class UserManagementView {
    public static VBox build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("User Management");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Create, activate, lock, and manage accounts");
        sub.getStyleClass().add("page-subtitle");
        Label status = new Label();
        root.getChildren().addAll(new VBox(2, title, sub),
                createUserForm(state, status), usersTable(state), status);
        return root;
    }

    private static HBox createUserForm(AppState state, Label status) {
        TextField username = new TextField();
        username.setPromptText("Username");
        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        ComboBox<StaffRole> role = new ComboBox<>();
        role.getItems().addAll(StaffRole.values());
        role.setValue(StaffRole.DOCTOR);
        Button create = new Button("Create User");
        create.getStyleClass().addAll("btn-primary", "btn-small");
        create.setOnAction(e -> {
            try {
                state.authenticationService.createAccount(
                        username.getText(), password.getText(),
                        role.getValue(), fullName.getText());
                username.clear();
                fullName.clear();
                password.clear();
                status.setStyle("-fx-text-fill:#4ADE80;-fx-font-size:12px;");
                status.setText("User created.");
            } catch (RuntimeException ex) {
                status.setStyle("-fx-text-fill:#F87171;-fx-font-size:12px;");
                status.setText(ex.getMessage());
            }
        });
        HBox form = new HBox(10, username, fullName, password, role, create);
        form.setAlignment(Pos.CENTER_LEFT);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(12));
        HBox.setHgrow(username, Priority.ALWAYS);
        return form;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static TableView<UserAccount> usersTable(AppState state) {
        TableView<UserAccount> table = new TableView<>();
        col(table, "ID", "id", 90);
        col(table, "Username", "username", 130);
        col(table, "Name", "displayName", 170);
        col(table, "Role", "role", 130);
        col(table, "Status", "status", 110);
        col(table, "Last Login", "lastLogin", 170);
        table.getItems().setAll(state.authenticationService.getAllAccounts());
        table.setPrefHeight(340);
        table.getStyleClass().add("table-view");
        HBox actions = new HBox(8,
                actionBtn("Activate", state, table, AccountStatus.ACTIVE),
                actionBtn("Deactivate", state, table, AccountStatus.INACTIVE),
                actionBtn("Lock", state, table, AccountStatus.LOCKED),
                actionBtn("Unlock", state, table, AccountStatus.ACTIVE));
        actions.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(8, table, actions);
        return table;
    }

    private static Button actionBtn(String text, AppState state,
                                    TableView<UserAccount> table, AccountStatus st) {
        Button b = new Button(text);
        b.getStyleClass().addAll("btn-secondary", "btn-small");
        b.setOnAction(e -> {
            UserAccount sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            try {
                state.authenticationService.setStatus(sel.getId(), st);
                table.getItems().setAll(state.authenticationService.getAllAccounts());
            } catch (RuntimeException ex) {
                throw ex;
            }
        });
        return b;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void col(TableView t, String name, String prop, double w) {
        TableColumn c = new TableColumn(name);
        c.setCellValueFactory(new PropertyValueFactory(prop));
        c.setPrefWidth(w);
        t.getColumns().add(c);
    }
}
