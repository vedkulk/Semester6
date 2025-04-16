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
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAssignmentsController {

    @FXML
    private ListView<String> assignmentsListView;

    @FXML
    public void initialize() {
        fetchAssignments();
    }

    private void fetchAssignments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.title, a.deadline, c.class_name " +
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
            
            assignmentsListView.setItems(assignments);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch assignments.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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

}
