package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteAssignmentController {

    @FXML
    private ComboBox<String> assignmentsComboBox;  // Change this to ListView<String> if you're using a ListView

    @FXML
    public void initialize() {
        if (assignmentsComboBox != null) {
            fetchAssignments();
        } else {
            showAlert("Error", "ComboBox is not initialized properly.");
        }
    }

    @FXML
    public void deleteAssignment() {
        String selectedAssignment = assignmentsComboBox.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            showAlert("Error", "Please select an assignment to delete.");
            return;
        }

        int assignmentId = Integer.parseInt(selectedAssignment.split(" - ")[0]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM assignments WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            stmt.executeUpdate();

            showAlert("Success", "Assignment deleted successfully!");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete assignment.");
        }
    }

    @FXML
    public void cancel() {
        closeWindow();
    }

    private void fetchAssignments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.id, a.title FROM assignments a " +
                    "JOIN classes c ON a.class_id = c.id " +
                    "WHERE c.teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, UserSession.getTeacherId());
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> assignments = FXCollections.observableArrayList();

            while (rs.next()) {
                int assignmentId = rs.getInt("id");
                String title = rs.getString("title");
                assignments.add(assignmentId + " - " + title); // Add assignment ID and title to the ComboBox
            }

            assignmentsComboBox.setItems(assignments);  // Populate the ComboBox with assignments
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch assignments.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) assignmentsComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void goBack() {
        closeWindow(); // Closes the current window and returns to the previous one
    }
}
