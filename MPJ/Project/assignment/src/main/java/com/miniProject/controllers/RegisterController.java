package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField nameField, emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        // Initialize the ComboBox with the roles
        ObservableList<String> roles = FXCollections.observableArrayList("STUDENT", "TEACHER");
        roleComboBox.setItems(roles);
    }

    public void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();  // Get selected role from ComboBox

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);  // Set the role chosen by the user
            stmt.executeUpdate();
            showAlert("Success", "User Registered!");

            // After successful registration, navigate to login screen
            redirectToLogin();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void redirectToLogin() {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            VBox loginView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) nameField.getScene().getWindow();
            
            // Set the scene to the login screen
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the login screen!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
