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
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;

public class TeacherViewSubmissionsController {

    @FXML
    private Button downloadButton;

    @FXML
    private TableView<ObservableList<String>> submissionsTable;

    @FXML
    private Button gradeButton, backButton;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadSubmissions();
    }

    private void loadSubmissions() {
        data.clear();
        submissionsTable.getColumns().clear();

        String query = """
            SELECT c.class_name, u.name AS student_name, a.title AS assignment_title,
                   CASE WHEN s.submission_time <= a.deadline THEN 'On Time' ELSE 'Late' END AS status,
                   s.file_path
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.id
            JOIN users u ON s.student_id = u.id AND u.role = 'STUDENT'
            JOIN classes c ON a.class_id = c.id
            WHERE c.teacher_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, UserSession.getTeacherId());
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            // Create columns dynamically based on metadata
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                final int colIndex = i;
                TableColumn<ObservableList<String>, String> column =
                        new TableColumn<>(metaData.getColumnLabel(i + 1));
                column.setCellValueFactory(param ->
                        new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
                submissionsTable.getColumns().add(column);
            }

            // Populate data
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            submissionsTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load submissions.");
        }
    }

    @FXML
    public void downloadSelectedFile() {
        ObservableList<String> selected = submissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a submission to download.");
            return;
        }

        String filePath = selected.get(4); // 5th column is file_path
        File sourceFile = new File(filePath);

        if (!sourceFile.exists()) {
            showAlert("File Not Found", "The file does not exist at:\n" + filePath);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Submission File");
        fileChooser.setInitialFileName(sourceFile.getName());
        File targetFile = fileChooser.showSaveDialog(submissionsTable.getScene().getWindow());

        if (targetFile != null) {
            try (InputStream in = new FileInputStream(sourceFile);
                 OutputStream out = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }

                showAlert("Download Successful", "File downloaded to:\n" + targetFile.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Download Error", "Could not save file.");
            }
        }
    }

    @FXML
    public void gradeSubmission() {
        ObservableList<String> selected = submissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a submission to grade.");
            return;
        }

        TextInputDialog gradeDialog = new TextInputDialog();
        gradeDialog.setTitle("Grade Submission");
        gradeDialog.setHeaderText("Enter marks for student: " + selected.get(1));
        gradeDialog.setContentText("Marks (e.g., 8/10):");

        gradeDialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split("/");
                int marks = Integer.parseInt(parts[0].trim());
                int maxMarks = Integer.parseInt(parts[1].trim());

                TextInputDialog feedbackDialog = new TextInputDialog();
                feedbackDialog.setTitle("Feedback");
                feedbackDialog.setHeaderText("Enter optional feedback:");
                feedbackDialog.setContentText("Feedback:");
                String feedback = feedbackDialog.showAndWait().orElse("");

                try (Connection conn = DatabaseConnection.getConnection()) {
                    // Get submission ID
                    String getSubmissionIdQuery = """
                        SELECT s.id FROM submissions s
                        JOIN assignments a ON s.assignment_id = a.id
                        JOIN users u ON s.student_id = u.id AND u.role = 'STUDENT'
                        WHERE a.title = ? AND u.name = ?
                    """;

                    PreparedStatement getStmt = conn.prepareStatement(getSubmissionIdQuery);
                    getStmt.setString(1, selected.get(2)); // assignment title
                    getStmt.setString(2, selected.get(1)); // student name
                    ResultSet rs = getStmt.executeQuery();

                    if (rs.next()) {
                        int submissionId = rs.getInt("id");

                        // Check if grade already exists
                        String checkQuery = "SELECT id FROM grades WHERE submission_id = ?";
                        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                        checkStmt.setInt(1, submissionId);
                        ResultSet gradeRS = checkStmt.executeQuery();

                        if (gradeRS.next()) {
                            // Update grade
                            String updateQuery = """
                                UPDATE grades
                                SET marks = ?, max_marks = ?, feedback = ?
                                WHERE submission_id = ?
                            """;
                            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                            updateStmt.setInt(1, marks);
                            updateStmt.setInt(2, maxMarks);
                            updateStmt.setString(3, feedback);
                            updateStmt.setInt(4, submissionId);
                            updateStmt.executeUpdate();
                        } else {
                            // Insert grade
                            String insertQuery = """
                                INSERT INTO grades (submission_id, marks, max_marks, feedback)
                                VALUES (?, ?, ?, ?)
                            """;
                            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                            insertStmt.setInt(1, submissionId);
                            insertStmt.setInt(2, marks);
                            insertStmt.setInt(3, maxMarks);
                            insertStmt.setString(4, feedback);
                            insertStmt.executeUpdate();
                        }

                        showAlert("Success", "Grade submitted successfully!");

                    } else {
                        showAlert("Error", "Submission not found in database.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Database Error", "Could not submit grade.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Input Error", "Invalid marks format or internal error.");
            }
        });
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/teacherDashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not return to dashboard.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
