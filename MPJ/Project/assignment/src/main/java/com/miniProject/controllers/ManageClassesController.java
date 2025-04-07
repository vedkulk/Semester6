package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ManageClassesController {

    @FXML
    private TextField classNameField;
    
    @FXML
    private ListView<String> classesListView;

    @FXML
    public void initialize() {
        fetchClasses();
    }

    @FXML
    public void createClass() {
        String className = classNameField.getText();
        if (className.isEmpty()) {
            showAlert("Error", "Class name cannot be empty.");
            return;
        }

        String classCode = UUID.randomUUID().toString().substring(0, 10);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO classes (class_name, class_code, teacher_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, className);
            stmt.setString(2, classCode);
            stmt.setInt(3, UserSession.getTeacherId());
            stmt.executeUpdate();

            showAlert("Success", "Class created successfully. Class code: " + classCode);
            classNameField.clear();
            fetchClasses();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create class.");
        }
    }

    @FXML
    public void deleteClass() {
        String selectedClass = classesListView.getSelectionModel().getSelectedItem();
        if (selectedClass == null) {
            showAlert("Error", "Please select a class to delete.");
            return;
        }

        int classId = Integer.parseInt(selectedClass.split(" - ")[0]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM classes WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.executeUpdate();

            showAlert("Success", "Class deleted successfully!");
            fetchClasses();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete class.");
        }
    }

    private void fetchClasses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, class_name, class_code FROM classes WHERE teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, UserSession.getTeacherId());
            ResultSet rs = stmt.executeQuery();
    
            classesListView.getItems().clear();
    
            while (rs.next()) {
                int classId = rs.getInt("id");
                String className = rs.getString("class_name");
                String classCode = rs.getString("class_code");
    
                // Show class name along with class code
                classesListView.getItems().add(classId + " - " + className + " (Code: " + classCode + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch classes.");
        }
    }
    @FXML
    public void goBack(ActionEvent event) {
        try {
            // Load the Teacher Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/teacherDashboard.fxml"));
            Parent registerView = loader.load();

            // Get the current stage and set the register scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(registerView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Teacher Dashboard.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
