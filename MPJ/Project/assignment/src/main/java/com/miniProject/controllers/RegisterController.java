package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

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
        String role = roleComboBox.getValue(); // Get selected role from ComboBox
    
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }
    
        // Check if the email already exists in the database
        if (isEmailExists(email)) {
            showAlert("Error", "Email is already registered!");
            return;
        }
    
        // Hash the password before storing it
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword); // Store hashed password
            stmt.setString(4, role); // Set the role chosen by the user
            stmt.executeUpdate();
            showAlert("Success", "User Registered!");
    
            // After successful registration, navigate to login screen
            redirectToLogin();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error! Please try again.");
        }
    }
    
    private boolean isPasswordValid(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);
    }

    private boolean isEmailExists(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Email already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void redirectToLogin() {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            AnchorPane loginView = loader.load(); // Ensure the login.fxml is using VBox

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
