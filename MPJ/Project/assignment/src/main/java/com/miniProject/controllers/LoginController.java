package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML
    private AnchorPane loginVBox;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public void initialize() {
        if (loginVBox == null) {
            System.err.println("VBox is not properly injected!");
            return;
        }

        // Get screen dimensions
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Set the preferred width and height of the VBox to 50% of the screen size
        loginVBox.setPrefWidth(screenWidth * 0.5);
        loginVBox.setPrefHeight(screenHeight * 0.5);
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Get the stored hashed password from the database
                String storedHashedPassword = rs.getString("password");

                // Check if the entered password matches the stored hashed password
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    // Successful login, now set user session based on role
                    String role = rs.getString("role");

                    // Set the session role and user ID
                    UserSession.setRole(role);
                    if ("STUDENT".equals(role)) {
                        UserSession.setStudentId(rs.getInt("id"));
                    } else if ("TEACHER".equals(role)) {
                        UserSession.setTeacherId(rs.getInt("id"));
                    } else {
                        showAlert("Error", "Invalid role in the system!");
                        return;
                    }

                    showAlert("Success", "Login Successful!");
                    // Redirect to the appropriate dashboard
                    redirectToDashboard();
                } else {
                    showAlert("Error", "Invalid email or password!");
                }
            } else {
                showAlert("Error", "Invalid email or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection issue. Please try again.");
        }
    }

    private void redirectToDashboard() {
        try {
            String fxmlFile;
            if ("STUDENT".equals(UserSession.getRole())) {
                fxmlFile = "/views/studentDashboard.fxml";
            } else if ("TEACHER".equals(UserSession.getRole())) {
                fxmlFile = "/views/teacherDashboard.fxml";
            } else {
                showAlert("Error", "Role not recognized. Please contact support.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent dashboardView = loader.load(); // ✅ No casting to VBox or AnchorPane

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(dashboardView);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the dashboard. Please try again.");
        }
    }

    @FXML
    public void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            AnchorPane registerView = loader.load(); // Use AnchorPane instead of VBox

            // Get the current stage and set the register scene
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(registerView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open registration screen.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
