package com.miniProject.controllers;

import com.miniProject.database.DatabaseConnection;
import com.miniProject.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.ListView;


public class TeacherDashboardController {

    @FXML
    private Button manageClassesButton, uploadAssignmentButton, viewSubmissionsButton, logoutButton, 
                   viewAssignmentsButton, deleteAssignmentButton;

    

    @FXML
    private ListView<String> assignmentsListView; // ✅ Now JavaFX will inject this properly


    
    @FXML
    public void openManageClasses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manageClasses.fxml"));
            VBox manageClassesView = loader.load(); // Load the FXML view
            Stage stage = (Stage) manageClassesButton.getScene().getWindow(); // Get the stage from the button

            // Set the scene to the loaded Manage Classes view
            Scene scene = new Scene(manageClassesView);
            stage.setScene(scene);
            stage.setTitle("Manage Classes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Manage Classes window.");
        }
    }

    @FXML
    public void goToUploadAssignment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/teacherUploadAssignment.fxml"));
            Stage stage = (Stage) uploadAssignmentButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the upload assignment screen.");
        }
    }

    @FXML
    public void viewSubmissions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/teacherViewSubmissions.fxml"));
            Stage stage = (Stage) viewSubmissionsButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("View Student Submissions");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the submissions screen.");
        }
    }


    @FXML
    public void goToViewAssignments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/viewAssignments.fxml"));
            Stage stage = new Stage();
            stage.setTitle("View Assignments");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the View Assignments window.");
        }
    }
    

    @FXML
    public void deleteAssignment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DeleteAssignment.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Delete Assignment");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open delete assignment window.");
        }
    }

    


    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            VBox loginView = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the login screen.");
        }
    }
    @FXML

    private void fetchAssignments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.id, a.title, a.deadline, c.class_name " +
                           "FROM assignments a " +
                           "JOIN classes c ON a.class_id = c.id " +
                           "WHERE c.teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, UserSession.getTeacherId());
            ResultSet rs = stmt.executeQuery();
    
            ObservableList<String> assignments = FXCollections.observableArrayList();
            
            while (rs.next()) {
                String title = rs.getString("title");
                String deadline = rs.getString("deadline");
                String className = rs.getString("class_name");
                assignments.add(title + " (Class: " + className + ", Due: " + deadline + ")");
            }
            
            assignmentsListView.setItems(assignments);  // ✅ Now this will work fine!
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
}
