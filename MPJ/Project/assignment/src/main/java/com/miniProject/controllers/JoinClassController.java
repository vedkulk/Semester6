package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JoinClassController {

    @FXML
    private TextField classCodeField;

    @FXML
    private ListView<String> enrolledClassesListView;

    private Map<String, Integer> classMap = new HashMap<>(); // maps "Class Name (code)" to class_id

    @FXML
    public void initialize() {
        loadEnrolledClasses();
    }

    private void loadEnrolledClasses() {
        enrolledClassesListView.getItems().clear();
        classMap.clear();
        int studentId = UserSession.getStudentId();

        String query = "SELECT c.id, c.class_name, c.class_code FROM classes c " +
                       "JOIN class_enrollments ce ON c.id = ce.class_id WHERE ce.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classId = rs.getInt("id");
                String name = rs.getString("class_name");
                String code = rs.getString("class_code");
                String display = name + " (" + code + ")";
                classMap.put(display, classId);
                enrolledClassesListView.getItems().add(display);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load enrolled classes.");
        }
    }

    @FXML
    public void handleJoinClass() {
        String classCode = classCodeField.getText();
        if (classCode.isEmpty()) {
            showAlert("Error", "Class code cannot be empty.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String findQuery = "SELECT id FROM classes WHERE class_code = ?";
            PreparedStatement stmt = conn.prepareStatement(findQuery);
            stmt.setString(1, classCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int classId = rs.getInt("id");

                String insertQuery = "INSERT IGNORE INTO class_enrollments (student_id, class_id) VALUES (?, ?)";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setInt(1, UserSession.getStudentId());
                stmt.setInt(2, classId);
                stmt.executeUpdate();

                showAlert("Success", "Successfully joined the class!");
                classCodeField.clear();
                loadEnrolledClasses();
            } else {
                showAlert("Error", "Invalid class code.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to join class.");
        }
    }

    @FXML
    public void handleLeaveClass() {
        String selected = enrolledClassesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a class to leave.");
            return;
        }

        int classId = classMap.get(selected);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM class_enrollments WHERE student_id = ? AND class_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteQuery);
            stmt.setInt(1, UserSession.getStudentId());
            stmt.setInt(2, classId);
            stmt.executeUpdate();

            showAlert("Success", "Successfully left the class.");
            loadEnrolledClasses();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not leave the class.");
        }
    }


    
    @FXML
private void goBack(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/studentDashboard.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not go back to dashboard.");
    }
}


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
