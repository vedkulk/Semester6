package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentAssignmentsController {

    @FXML
    private ListView<String> assignmentsListView;

    @FXML
    private Button downloadAssignmentButton, chooseFileButton, submitAssignmentButton, backButton;

    @FXML
    private Label selectedFileLabel;

    private File selectedFile;

    @FXML
    public void initialize() {
        loadAssignments();
    }

    private void loadAssignments() {
        int studentId = UserSession.getStudentId();
        String query = """
            SELECT a.id, a.title, a.file_path 
            FROM assignments a
            JOIN class_enrollments ce ON a.class_id = ce.class_id
            WHERE ce.student_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            assignmentsListView.getItems().clear();
            while (rs.next()) {
                int assignmentId = rs.getInt("id");
                String title = rs.getString("title");
                String filePath = rs.getString("file_path");

                // Show assignment ID and title, and store file path in parentheses
                assignmentsListView.getItems().add(assignmentId + ": " + title + " (File: " + filePath + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load assignments.");
        }
    }

    @FXML
    public void downloadAssignment() {
        String selectedItem = assignmentsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select an assignment to download.");
            return;
        }

        // Extract file path from selected item string
        String filePath = selectedItem.substring(selectedItem.indexOf("(File: ") + 7, selectedItem.length() - 1);
        File file = new File(filePath);

        if (!file.exists()) {
            showAlert("Error", "File not found on server.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(file.getName());
        File destination = fileChooser.showSaveDialog(assignmentsListView.getScene().getWindow());

        if (destination != null) {
            try {
                Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert("Success", "Assignment downloaded successfully to:\n" + destination.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not download the file.");
            }
        }
    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File to Submit");
        selectedFile = fileChooser.showOpenDialog(assignmentsListView.getScene().getWindow());

        if (selectedFile != null) {
            selectedFileLabel.setText("Selected: " + selectedFile.getName());
        }
    }

    @FXML
    public void submitAssignment() {
        if (selectedFile == null) {
            showAlert("Error", "Please choose a file to submit.");
            return;
        }

        String selectedItem = assignmentsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Error", "Please select an assignment to submit.");
            return;
        }

        int assignmentId;
        try {
            assignmentId = Integer.parseInt(selectedItem.split(":")[0].trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid assignment selected.");
            return;
        }

        // Create submissions directory if it doesn't exist
        File submissionsDir = new File("submissions");
        if (!submissionsDir.exists()) {
            submissionsDir.mkdirs();
        }

        // Define file name with student ID prefix and save in "submissions" directory
        String fileName = UserSession.getStudentId() + "_" + selectedFile.getName();
        File destinationFile = new File(submissionsDir, fileName);
        String relativePath = "submissions/" + fileName;

        try {
            // Copy file to submissions folder
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if the student already submitted this assignment
                String checkQuery = "SELECT id FROM submissions WHERE assignment_id = ? AND student_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, assignmentId);
                checkStmt.setInt(2, UserSession.getStudentId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    showAlert("Error", "You have already submitted this assignment.");
                    return;
                }

                // Insert submission record
                String insertQuery = "INSERT INTO submissions (assignment_id, student_id, file_path) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, assignmentId);
                insertStmt.setInt(2, UserSession.getStudentId());
                insertStmt.setString(3, relativePath);
                insertStmt.executeUpdate();

                showAlert("Success", "Assignment submitted successfully.");
                selectedFileLabel.setText("No file selected");
                selectedFile = null;
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit assignment.");
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StudentDashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load the dashboard screen.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
