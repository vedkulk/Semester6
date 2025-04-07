package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class StudentSubmittedAssignmentsController {

    @FXML
    private ListView<String> submittedListView;

    private final ObservableList<String> submittedAssignments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadSubmittedAssignments();
    }

    private void loadSubmittedAssignments() {
        submittedAssignments.clear();

        String query = """
            SELECT s.id AS submission_id, a.title, s.file_path
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.id
            LEFT JOIN grades g ON g.submission_id = s.id
            WHERE s.student_id = ? AND g.id IS NULL
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, UserSession.getStudentId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int submissionId = rs.getInt("submission_id");
                String assignmentTitle = rs.getString("title");
                String filePath = rs.getString("file_path");
                submittedAssignments.add(submissionId + ": " + assignmentTitle + " (" + filePath + ")");
            }

            submittedListView.setItems(submittedAssignments);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load submissions.");
        }
    }

    @FXML
    public void deleteSelectedSubmission() {
        String selectedItem = submittedListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select a submission to delete.");
            return;
        }

        int submissionId;
        String filePath;

        try {
            submissionId = Integer.parseInt(selectedItem.split(":")[0].trim());
            filePath = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")"));
        } catch (Exception e) {
            showAlert("Error", "Invalid selection format.");
            return;
        }

        // Delete file
        File file = new File(filePath);
        if (file.exists()) file.delete();

        // Delete from DB
        String deleteQuery = "DELETE FROM submissions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, submissionId);
            stmt.executeUpdate();
            showAlert("Deleted", "Submission deleted successfully.");
            loadSubmittedAssignments();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete submission.");
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/studentDashboard.fxml"));
            Stage stage = (Stage) submittedListView.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
