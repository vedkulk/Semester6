package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherUploadAssignmentController {

    @FXML
    private ComboBox<String> classDropdown;
    @FXML
    private TextField assignmentTitleField;
    @FXML
    private TextArea assignmentDescriptionField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private Button selectFileButton, uploadAssignmentButton, backButton;
    @FXML
    private Label selectedFileLabel;

    private File selectedFile;
    private int selectedClassId;

    @FXML
    public void initialize() {
        loadTeacherClasses();
        classDropdown.setOnAction(e -> setSelectedClassId());
    }

    private void loadTeacherClasses() {
        List<String> classList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, class_name FROM classes WHERE teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, UserSession.getTeacherId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classId = rs.getInt("id");
                String className = rs.getString("class_name");
                classList.add(classId + " - " + className);
            }
            ObservableList<String> classOptions = FXCollections.observableArrayList(classList);
            classDropdown.setItems(classOptions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSelectedClassId() {
        String selected = classDropdown.getValue();
        if (selected != null) {
            selectedClassId = Integer.parseInt(selected.split(" - ")[0]);
        }
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF or TXT files", "*.pdf", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedFileLabel.setText("Selected: " + selectedFile.getName());
        } else {
            selectedFileLabel.setText("No file selected");
        }
    }

    @FXML
    public void uploadAssignment() {
        String title = assignmentTitleField.getText();
        String description = assignmentDescriptionField.getText();
        LocalDate deadline = deadlinePicker.getValue();

        if (title.isEmpty() || description.isEmpty() || selectedFile == null || deadline == null || selectedClassId == 0) {
            showAlert("Error", "Please fill in all fields and select a file.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO assignments (class_id, title, description, deadline, file_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedClassId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setObject(4, deadline);
            stmt.setString(5, selectedFile.getAbsolutePath());
            stmt.executeUpdate();
            
            showAlert("Success", "Assignment uploaded successfully!");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to upload assignment.");
        }
    }

    private void clearFields() {
        assignmentTitleField.clear();
        assignmentDescriptionField.clear();
        deadlinePicker.setValue(null);
        selectedFile = null;
        selectedFileLabel.setText("No file selected");
        classDropdown.getSelectionModel().clearSelection();
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
            showAlert("Error", "Could not load the dashboard.");
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
