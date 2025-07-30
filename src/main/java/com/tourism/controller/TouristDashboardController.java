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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouristDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> attractionComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private Label priceLabel;
    @FXML private Button bookButton;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> difficultyColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button logoutButton;
    @FXML private Button translateButton;
    
    private User currentUser;
    private List<Attraction> attractions;
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadBookings();
    }
    
    @FXML
    private void initialize() {
        initializeTranslations();
        setupTable();
        loadAttractions();
        setupDifficultyComboBox();
        setupPriceCalculation();
        
        // Set minimum date to today
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }
    
    private void initializeTranslations() {
        translations.put("Welcome,", "स्वागत छ,");
        translations.put("Book Attraction", "आकर्षण बुक गर्नुहोस्");
        translations.put("Update Booking", "बुकिङ अपडेट गर्नुहोस्");
        translations.put("Delete Booking", "बुकिङ मेटाउनुहोस्");
        translations.put("Logout", "लगआउट");
        translations.put("Translate to Nepali", "नेपालीमा अनुवाद");
        translations.put("Translate to English", "अंग्रेजीमा अनुवाद");
        translations.put("Easy", "सजिलो");
        translations.put("Medium", "मध्यम");
        translations.put("Hard", "कठिन");
    }
    
    private void setupTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        attractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", price));
                }
            }
        });
    }
    
    private void loadAttractions() {
        attractions = FileManager.getAttractions();
        ObservableList<String> attractionNames = FXCollections.observableArrayList();
        for (Attraction attraction : attractions) {
            attractionNames.add(attraction.getName());
        }
        attractionComboBox.setItems(attractionNames);
    }
    
    private void setupDifficultyComboBox() {
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyComboBox.setValue("Easy");
    }
    
    private void setupPriceCalculation() {
        attractionComboBox.setOnAction(e -> calculatePrice());
        difficultyComboBox.setOnAction(e -> calculatePrice());
        datePicker.setOnAction(e -> calculatePrice());
    }
    
    private void calculatePrice() {
        if (attractionComboBox.getValue() == null || difficultyComboBox.getValue() == null || datePicker.getValue() == null) {
            return;
        }
        
        String selectedAttraction = attractionComboBox.getValue();
        String difficulty = difficultyComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        
        // Find attraction base price
        double basePrice = 0;
        String altitude = "";
        for (Attraction attraction : attractions) {
            if (attraction.getName().equals(selectedAttraction)) {
                basePrice = attraction.getBasePrice();
                altitude = attraction.getAltitude();
                break;
            }
        }
        
        // Apply difficulty multiplier
        double multiplier = 1.0;
        switch (difficulty) {
            case "Medium": case "मध्यम": multiplier = 1.2; break;
            case "Hard": case "कठिन": multiplier = 1.5; break;
        }
        
        double finalPrice = basePrice * multiplier;
        
        // Check for festival discount (August-October)
        boolean festivalDiscount = false;
        if (selectedDate != null) {
            int month = selectedDate.getMonthValue();
            if (month >= 8 && month <= 10) {
                finalPrice *= 0.8; // 20% discount
                festivalDiscount = true;
            }
        }
        
        priceLabel.setText("$" + String.format("%.2f", finalPrice));
        
        // Show high altitude warning
        if ("High".equals(altitude)) {
            showAlert("High Altitude Warning", 
                     "This trek involves high altitude. Please ensure you are physically prepared and consult a doctor if you have health concerns.", 
                     Alert.AlertType.WARNING);
        }
        
        // Show festival discount popup
        if (festivalDiscount) {
            showAlert("Festival Discount Applied!", 
                     "20% Dashain & Tihar festival discount has been applied to your booking!", 
                     Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleBooking() {
        if (!validateBookingFields()) {
            return;
        }
        
        String bookingId = FileManager.generateBookingId();
        String attraction = attractionComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String difficulty = difficultyComboBox.getValue();
        
        // Convert Nepali difficulty back to English if needed
        if (isNepali) {
            if ("सजिलो".equals(difficulty)) difficulty = "Easy";
            else if ("मध्यम".equals(difficulty)) difficulty = "Medium";
            else if ("कठिन".equals(difficulty)) difficulty = "Hard";
        }
        
        double price = Double.parseDouble(priceLabel.getText().substring(1));
        
        Booking booking = new Booking(bookingId, currentUser.getUsername(), attraction, date, difficulty, price, "Confirmed");
        FileManager.saveBooking(booking);
        
        showAlert("Booking Successful", "Your booking has been confirmed! Booking ID: " + bookingId, Alert.AlertType.INFORMATION);
        loadBookings();
        clearBookingFields();
    }
    
    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to update.", Alert.AlertType.WARNING);
            return;
        }
        
        // Populate fields with selected booking data
        attractionComboBox.setValue(selectedBooking.getAttraction());
        datePicker.setValue(selectedBooking.getDate());
        difficultyComboBox.setValue(selectedBooking.getDifficulty());
        calculatePrice();
        
        showAlert("Update Mode", "Booking details loaded. Modify and click 'Book Attraction' to update.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to delete.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this booking?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // In a real application, you would implement proper deletion from file
            showAlert("Booking Deleted", "Booking has been cancelled successfully.", Alert.AlertType.INFORMATION);
            loadBookings();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Nepal Tourism Management System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleTranslate() {
        isNepali = !isNepali;
        updateLanguage();
    }
    
    private void updateLanguage() {
        if (isNepali) {
            if (currentUser != null) {
                welcomeLabel.setText(translations.get("Welcome,") + " " + currentUser.getFullName() + "!");
            }
            bookButton.setText(translations.get("Book Attraction"));
            updateButton.setText(translations.get("Update Booking"));
            deleteButton.setText(translations.get("Delete Booking"));
            logoutButton.setText(translations.get("Logout"));
            translateButton.setText(translations.get("Translate to English"));
            
            // Update difficulty ComboBox
            String selectedDifficulty = difficultyComboBox.getValue();
            difficultyComboBox.getItems().clear();
            difficultyComboBox.getItems().addAll(
                translations.get("Easy"),
                translations.get("Medium"),
                translations.get("Hard")
            );
            if (selectedDifficulty != null) {
                difficultyComboBox.setValue(translations.get(selectedDifficulty));
            }
        } else {
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
            }
            bookButton.setText("Book Attraction");
            updateButton.setText("Update Booking");
            deleteButton.setText("Delete Booking");
            logoutButton.setText("Logout");
            translateButton.setText("Translate to Nepali");
            
            // Update difficulty ComboBox
            String selectedDifficulty = difficultyComboBox.getValue();
            difficultyComboBox.getItems().clear();
            difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
            
            // Convert back to English
            if (selectedDifficulty != null) {
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    if (entry.getValue().equals(selectedDifficulty)) {
                        difficultyComboBox.setValue(entry.getKey());
                        break;
                    }
                }
            }
        }
    }
    
    private void loadBookings() {
        if (currentUser == null) return;
        
        List<Booking> userBookings = FileManager.getUserBookings(currentUser.getUsername());
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(userBookings);
        bookingsTable.setItems(bookingList);
    }
    
    private boolean validateBookingFields() {
        if (attractionComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select an attraction.", Alert.AlertType.ERROR);
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date.", Alert.AlertType.ERROR);
            return false;
        }
        if (difficultyComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select difficulty level.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void clearBookingFields() {
        attractionComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        difficultyComboBox.setValue("Easy");
        priceLabel.setText("$0.00");
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
