package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.model.Attraction;
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

public class TouristDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> nameColumn;
    @FXML private TableColumn<Attraction, String> altitudeColumn;
    @FXML private TableColumn<Attraction, String> difficultyColumn;
    @FXML private TableColumn<Attraction, Double> priceColumn;
    @FXML private DatePicker bookingDatePicker;
    @FXML private Button bookButton;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, Double> bookingPriceColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private Label messageLabel;
    @FXML private Button translateButton;
    @FXML private Button logoutButton;

    private User currentUser;
    private boolean isNepali = false;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName());
        loadAttractions();
        loadUserBookings();
    }

    @FXML
    private void initialize() {
        // Initialize attractions table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        altitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        // Initialize bookings table
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        attractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        bookingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    private void handleBooking() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = bookingDatePicker.getValue();

        if (selectedAttraction == null) {
            messageLabel.setText(isNepali ? "कृपया आकर्षण चयन गर्नुहोस्" : "Please select an attraction");
            return;
        }

        if (selectedDate == null) {
            messageLabel.setText(isNepali ? "कृपया मिति चयन गर्नुहोस्" : "Please select a date");
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            messageLabel.setText(isNepali ? "कृपया भविष्यको मिति चयन गर्नुहोस्" : "Please select a future date");
            return;
        }

        // Calculate price with festival discount
        double price = selectedAttraction.getBasePrice();
        boolean festivalDiscount = false;
        int month = selectedDate.getMonthValue();
        if (month >= 8 && month <= 10) { // August to October
            price *= 0.8; // 20% discount
            festivalDiscount = true;
        }

        // Check for high altitude warning
        if ("High".equals(selectedAttraction.getAltitude())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(isNepali ? "उच्च उचाइ चेतावनी" : "High Altitude Warning");
            alert.setHeaderText(isNepali ? "सुरक्षा चेतावनी" : "Safety Warning");
            alert.setContentText(isNepali ? 
                "यो उच्च उचाइको ट्रेक हो। कृपया उचित तयारी गर्नुहोस् र स्वास्थ्य सावधानी अपनाउनुहोस्।" :
                "This is a high altitude trek. Please ensure proper preparation and health precautions.");
            alert.showAndWait();
        }

        // Create booking
        String bookingId = FileManager.generateBookingId();
        Booking booking = new Booking(bookingId, currentUser.getUsername(), selectedAttraction.getName(),
                selectedDate, selectedAttraction.getDifficulty(), price, "Pending");
        booking.setFestivalDiscount(festivalDiscount);

        FileManager.saveBooking(booking);
        
        String discountMsg = festivalDiscount ? 
            (isNepali ? " (२०% चाडपर्व छुट लागू)" : " (20% Festival Discount Applied)") : "";
        messageLabel.setText((isNepali ? "बुकिङ सफल! मूल्य: $" : "Booking successful! Price: $") + 
                           String.format("%.2f", price) + discountMsg);
        
        loadUserBookings();
    }

    @FXML
    private void handleTranslate() {
        isNepali = !isNepali;
        if (isNepali) {
            welcomeLabel.setText("स्वागत छ, " + currentUser.getFullName());
            bookButton.setText("बुक गर्नुहोस्");
            logoutButton.setText("लगआउट");
            translateButton.setText("अंग्रेजीमा अनुवाद गर्नुहोस्");
        } else {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName());
            bookButton.setText("Book Now");
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

    private void loadAttractions() {
        List<Attraction> attractions = FileManager.getAttractions();
        ObservableList<Attraction> attractionList = FXCollections.observableArrayList(attractions);
        attractionsTable.setItems(attractionList);
    }

    private void loadUserBookings() {
        List<Booking> bookings = FileManager.getUserBookings(currentUser.getUsername());
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(bookingList);
    }
}
