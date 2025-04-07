package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDashboardController {

    @FXML
    private Button viewClassesButton, submitAssignmentButton, viewGradesButton, logoutButton, joinClassButton, downloadAssignmentButton;

    @FXML
    private TextField classCodeField;

    @FXML
    private ListView<String> assignmentsListView;

    @FXML
    private Label selectedFileLabel;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }

    @FXML
    public void viewClasses() {
        int studentId = UserSession.getStudentId();
        StringBuilder classList = new StringBuilder();

        String query = "SELECT c.class_name, c.class_code FROM classes c " +
                       "JOIN class_enrollments ce ON c.id = ce.class_id " +
                       "WHERE ce.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String className = rs.getString("class_name");
                String classCode = rs.getString("class_code");
                classList.append("• ").append(className).append(" (").append(classCode).append(")\n");
            }

            if (classList.length() == 0) {
                showAlert("No Classes", "You are not enrolled in any classes.");
            } else {
                showAlert("Enrolled Classes", classList.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not retrieve classes.");
        }
    }

    @FXML
public void submitAssignment() {
    // Check if file is selected
    if (selectedFile == null) {
        showAlert("Error", "Please choose a file to submit.");
        return;
    }

    // Check if an assignment is selected
    String selectedItem = assignmentsListView.getSelectionModel().getSelectedItem();
    if (selectedItem == null) {
        showAlert("Error", "Please select an assignment from the list.");
        return;
    }

    int selectedAssignmentId;
    try {
        selectedAssignmentId = Integer.parseInt(selectedItem.split(":")[0].trim());
    } catch (NumberFormatException e) {
        showAlert("Error", "Invalid assignment selection.");
        return;
    }

    // Ensure the submissions directory exists
    File submissionsDir = new File("submissions");
    if (!submissionsDir.exists()) {
        submissionsDir.mkdirs();
    }

    String submissionPath = "submissions/" + UserSession.getStudentId() + "_" + selectedFile.getName();
    File destinationFile = new File(submissionPath);

    try {
        // Copy file to submission folder
        Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check for duplicate submission
            String checkQuery = "SELECT id FROM submissions WHERE assignment_id = ? AND student_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedAssignmentId);
            checkStmt.setInt(2, UserSession.getStudentId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                showAlert("Error", "You have already submitted this assignment.");
                return;
            }

            // Insert submission record
            String query = "INSERT INTO submissions (assignment_id, student_id, file_path) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedAssignmentId);
            stmt.setInt(2, UserSession.getStudentId());
            stmt.setString(3, submissionPath);
            stmt.executeUpdate();

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
    public void downloadAssignment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StudentAssignments.fxml"));
            Stage stage = (Stage) downloadAssignmentButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the download assignment screen.");
        }
    }

    @FXML
public void viewGrades() {
    int studentId = UserSession.getStudentId();
    StringBuilder gradeList = new StringBuilder();

    String query = """
        SELECT c.class_name, a.title AS assignment_title, 
               g.marks, g.max_marks, COALESCE(g.feedback, 'No feedback') AS feedback
        FROM grades g
        JOIN submissions s ON g.submission_id = s.id
        JOIN assignments a ON s.assignment_id = a.id
        JOIN classes c ON a.class_id = c.id
        WHERE s.student_id = ?
    """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            gradeList.append("📘 Class: ").append(rs.getString("class_name")).append("\n")
                     .append("📄 Assignment: ").append(rs.getString("assignment_title")).append("\n")
                     .append("✅ Marks: ").append(rs.getInt("marks"))
                     .append(" / ").append(rs.getInt("max_marks")).append("\n")
                     .append("💬 Feedback: ").append(rs.getString("feedback")).append("\n\n");
        }

        if (gradeList.length() == 0) {
            showAlert("Grades", "No grades available yet.");
        } else {
            showAlert("Your Grades", gradeList.toString());
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Could not retrieve grades.");
    }
}
    

    @FXML
    public void logout() {
        try {
            UserSession.reset();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the login screen.");
        }
    }
    @FXML
public void deleteSubmission() {
    String selectedItem = assignmentsListView.getSelectionModel().getSelectedItem();
    if (selectedItem == null) {
        showAlert("Error", "Please select an assignment to delete submission.");
        return;
    }

    int selectedAssignmentId;
    try {
        selectedAssignmentId = Integer.parseInt(selectedItem.split(":")[0].trim());
    } catch (NumberFormatException e) {
        showAlert("Error", "Invalid assignment selection.");
        return;
    }

    int studentId = UserSession.getStudentId();

    String fetchQuery = "SELECT id, file_path FROM submissions WHERE assignment_id = ? AND student_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement fetchStmt = conn.prepareStatement(fetchQuery)) {

        fetchStmt.setInt(1, selectedAssignmentId);
        fetchStmt.setInt(2, studentId);
        ResultSet rs = fetchStmt.executeQuery();

        if (!rs.next()) {
            showAlert("Not Found", "No submission found for this assignment.");
            return;
        }

        int submissionId = rs.getInt("id");
        String filePath = rs.getString("file_path");

        // First delete the file
        File submissionFile = new File(filePath);
        if (submissionFile.exists()) {
            submissionFile.delete(); // You can check the result if needed
        }

        // Now delete the submission record
        String deleteQuery = "DELETE FROM submissions WHERE id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, submissionId);
            deleteStmt.executeUpdate();
            showAlert("Deleted", "Submission deleted successfully.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to delete submission.");
    }
}

    @FXML
    public void joinClass() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/joinClass.fxml"));
            Stage stage = (Stage) joinClassButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the join class screen.");
        }
    }
    @FXML
    public void openMySubmissions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StudentSubmittedAssignments.fxml"));
            Stage stage = (Stage) viewClassesButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open submissions screen.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper method to set the file from file chooser (called from another controller)
    public void setSelectedFile(File file) {
        this.selectedFile = file;
        if (file != null && selectedFileLabel != null) {
            selectedFileLabel.setText(file.getName());
        }
    }
}
