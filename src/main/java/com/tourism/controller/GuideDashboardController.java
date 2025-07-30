package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.model.Booking;
import com.tourism.util.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GuideDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    @FXML private TableView<Booking> assignedBookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateStatusButton;
    @FXML private Label messageLabel;
    @FXML private Button translateButton;
    @FXML private Button logoutButton;

    private User currentUser;
    private boolean isNepali = false;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome Guide, " + user.getFullName());
        languagesLabel.setText("Languages: " + user.getLanguages());
        experienceLabel.setText("Experience: " + user.getExperience());
        loadAssignedBookings();
    }

    @FXML
    private void initialize() {
        // Initialize bookings table
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristColumn.setCellValueFactory(new PropertyValueFactory<>("tourist"));
        attractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize status combo box
        statusComboBox.getItems().addAll("Pending", "Confirmed", "In Progress", "Completed", "Cancelled");
    }

    @FXML
    private void handleUpdateStatus() {
        Booking selectedBooking = assignedBookingsTable.getSelectionModel().getSelectedItem();
        String newStatus = statusComboBox.getValue();

        if (selectedBooking == null) {
            messageLabel.setText(isNepali ? "कृपया बुकिङ चयन गर्नुहोस्" : "Please select a booking");
            return;
        }

        if (newStatus == null) {
            messageLabel.setText(isNepali ? "कृपया स्थिति चयन गर्नुहोस्" : "Please select a status");
            return;
        }

        // Update booking status (in a real application, this would update the file)
        selectedBooking.setStatus(newStatus);
        messageLabel.setText(isNepali ? "स्थिति अपडेट गरियो" : "Status updated successfully");
        
        // Refresh table
        loadAssignedBookings();
    }

    @FXML
    private void handleTranslate() {
        isNepali = !isNepali;
        if (isNepali) {
            welcomeLabel.setText("स्वागत गाइड, " + currentUser.getFullName());
            languagesLabel.setText("भाषाहरू: " + currentUser.getLanguages());
            experienceLabel.setText("अनुभव: " + currentUser.getExperience());
            updateStatusButton.setText("स्थिति अपडेट गर्नुहोस्");
            logoutButton.setText("लगआउट");
            translateButton.setText("अंग्रेजीमा अनुवाद गर्नुहोस्");
        } else {
            welcomeLabel.setText("Welcome Guide, " + currentUser.getFullName());
            languagesLabel.setText("Languages: " + currentUser.getLanguages());
            experienceLabel.setText("Experience: " + currentUser.getExperience());
            updateStatusButton.setText("Update Status");
            logoutButton.setText("Logout");
            translateButton.setText("Translate to Nepali");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAssignedBookings() {
        List<Booking> allBookings = FileManager.getBookings();
        // In a real application, you would filter bookings assigned to this guide
        // For now, we'll show bookings that need guides (status = "Pending")
        List<Booking> assignedBookings = allBookings.stream()
                .filter(booking -> "Pending".equals(booking.getStatus()) || 
                                 currentUser.getUsername().equals(booking.getGuide()))
                .collect(Collectors.toList());
        
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(assignedBookings);
        assignedBookingsTable.setItems(bookingList);
    }
}
