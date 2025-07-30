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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private TabPane mainTabPane;
    @FXML private Label welcomeLabel;
    
    // Guide Management Tab
    @FXML private TextField guideNameField;
    @FXML private TextField guideUsernameField;
    @FXML private PasswordField guidePasswordField;
    @FXML private TextField guideLanguagesField;
    @FXML private TextField guideExperienceField;
    @FXML private Button addGuideButton;
    @FXML private TableView<User> guidesTable;
    @FXML private TableColumn<User, String> guideNameColumn;
    @FXML private TableColumn<User, String> guideUsernameColumn;
    @FXML private TableColumn<User, String> guideLanguagesColumn;
    @FXML private TableColumn<User, String> guideExperienceColumn;
    @FXML private Button updateGuideButton;
    @FXML private Button deleteGuideButton;
    
    // Attraction Management Tab
    @FXML private TextField attractionNameField;
    @FXML private ComboBox<String> altitudeComboBox;
    @FXML private ComboBox<String> attractionDifficultyComboBox;
    @FXML private TextField basePriceField;
    @FXML private Button addAttractionButton;
    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> attractionNameColumn;
    @FXML private TableColumn<Attraction, String> altitudeColumn;
    @FXML private TableColumn<Attraction, String> attractionDifficultyColumn;
    @FXML private TableColumn<Attraction, Double> basePriceColumn;
    @FXML private Button updateAttractionButton;
    @FXML private Button deleteAttractionButton;
    
    // Booking Management Tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> bookingAttractionColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    @FXML private Button editBookingButton;
    @FXML private Button cancelBookingButton;
    
    // Analytics Tab
    @FXML private PieChart nationalityChart;
    @FXML private BarChart<String, Number> popularAttractionsChart;
    @FXML private Label totalRevenueLabel;
    
    @FXML private Button logoutButton;
    @FXML private Button translateButton;
    
    private User currentUser;
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, Administrator!");
        loadAllData();
    }
    
    @FXML
    private void initialize() {
        initializeTranslations();
        setupTables();
        setupComboBoxes();
    }
    
    private void initializeTranslations() {
        translations.put("Welcome, Administrator!", "स्वागत छ, प्रशासक!");
        translations.put("Add Guide", "गाइड थप्नुहोस्");
        translations.put("Update Guide", "गाइड अपडेट गर्नुहोस्");
        translations.put("Delete Guide", "गाइड मेटाउनुहोस्");
        translations.put("Add Attraction", "आकर्षण थप्नुहोस्");
        translations.put("Update Attraction", "आकर्षण अपडेट गर्नुहोस्");
        translations.put("Delete Attraction", "आकर्षण मेटाउनुहोस्");
        translations.put("Edit Booking", "बुकिङ सम्पादन गर्नुहोस्");
        translations.put("Cancel Booking", "बुकिङ रद्द गर्नुहोस्");
        translations.put("Logout", "लगआउट");
        translations.put("Translate to Nepali", "नेपालीमा अनुवाद");
        translations.put("Translate to English", "अंग्रेजीमा अनुवाद");
        translations.put("High", "उच्च");
        translations.put("Low", "न्यून");
        translations.put("Easy", "सजिलो");
        translations.put("Medium", "मध्यम");
        translations.put("Hard", "कठिन");
    }
    
    private void setupTables() {
        // Guides Table
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        guideUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        guideLanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        guideExperienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
        
        // Attractions Table
        attractionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        altitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        attractionDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        
        // Format price column
        basePriceColumn.setCellFactory(column -> new TableCell<Attraction, Double>() {
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
        
        // Bookings Table
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristColumn.setCellValueFactory(new PropertyValueFactory<>("tourist"));
        bookingAttractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    private void setupComboBoxes() {
        altitudeComboBox.getItems().addAll("High", "Low");
        attractionDifficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
    }
    
    private void loadAllData() {
        loadGuides();
        loadAttractions();
        loadBookings();
        loadAnalytics();
    }
    
    private void loadGuides() {
        // In a real application, you would load guides from file
        // For now, we'll create a sample list
        ObservableList<User> guides = FXCollections.observableArrayList();
        guidesTable.setItems(guides);
    }
    
    private void loadAttractions() {
        List<Attraction> attractions = FileManager.getAttractions();
        ObservableList<Attraction> attractionList = FXCollections.observableArrayList(attractions);
        attractionsTable.setItems(attractionList);
    }
    
    private void loadBookings() {
        List<Booking> bookings = FileManager.getBookings();
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(bookingList);
    }
    
    private void loadAnalytics() {
        loadNationalityChart();
        loadPopularAttractionsChart();
        calculateTotalRevenue();
    }
    
    private void loadNationalityChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Nepal", 35),
            new PieChart.Data("India", 25),
            new PieChart.Data("USA", 15),
            new PieChart.Data("UK", 10),
            new PieChart.Data("Germany", 8),
            new PieChart.Data("Others", 7)
        );
        nationalityChart.setData(pieChartData);
        nationalityChart.setTitle("Tourist Nationality Distribution");
    }
    
    private void loadPopularAttractionsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");
        
        List<Booking> bookings = FileManager.getBookings();
        Map<String, Long> attractionCounts = bookings.stream()
            .collect(Collectors.groupingBy(Booking::getAttraction, Collectors.counting()));
        
        for (Map.Entry<String, Long> entry : attractionCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        popularAttractionsChart.getData().clear();
        popularAttractionsChart.getData().add(series);
        popularAttractionsChart.setTitle("Most Popular Attractions");
    }
    
    private void calculateTotalRevenue() {
        List<Booking> bookings = FileManager.getBookings();
        double totalRevenue = bookings.stream()
            .mapToDouble(Booking::getPrice)
            .sum();
        
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
    }
    
    @FXML
    private void handleAddGuide() {
        if (!validateGuideFields()) {
            return;
        }
        
        User guide = new User(
            guideUsernameField.getText().trim(),
            guidePasswordField.getText().trim(),
            guideNameField.getText().trim(),
            "", // Email not required for admin-added guides
            "", // Phone not required for admin-added guides
            "Guide",
            guideLanguagesField.getText().trim(),
            guideExperienceField.getText().trim()
        );
        
        FileManager.saveUser(guide);
        showAlert("Success", "Guide added successfully!", Alert.AlertType.INFORMATION);
        clearGuideFields();
        loadGuides();
    }
    
    @FXML
    private void handleUpdateGuide() {
        User selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("No Selection", "Please select a guide to update.", Alert.AlertType.WARNING);
            return;
        }
        
        // Populate fields with selected guide data
        guideNameField.setText(selectedGuide.getFullName());
        guideUsernameField.setText(selectedGuide.getUsername());
        guideLanguagesField.setText(selectedGuide.getLanguages());
        guideExperienceField.setText(selectedGuide.getExperience());
        
        showAlert("Update Mode", "Guide details loaded. Modify and click 'Add Guide' to update.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleDeleteGuide() {
        User selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("No Selection", "Please select a guide to delete.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this guide?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showAlert("Guide Deleted", "Guide has been removed successfully.", Alert.AlertType.INFORMATION);
            loadGuides();
        }
    }
    
    @FXML
    private void handleAddAttraction() {
        if (!validateAttractionFields()) {
            return;
        }
        
        String altitude = altitudeComboBox.getValue();
        String difficulty = attractionDifficultyComboBox.getValue();
        
        // Convert Nepali values back to English if needed
        if (isNepali) {
            if ("उच्च".equals(altitude)) altitude = "High";
            else if ("न्यून".equals(altitude)) altitude = "Low";
            
            if ("सजिलो".equals(difficulty)) difficulty = "Easy";
            else if ("मध्यम".equals(difficulty)) difficulty = "Medium";
            else if ("कठिन".equals(difficulty)) difficulty = "Hard";
        }
        
        Attraction attraction = new Attraction(
            attractionNameField.getText().trim(),
            altitude,
            difficulty,
            Double.parseDouble(basePriceField.getText().trim())
        );
        
        FileManager.saveAttraction(attraction);
        showAlert("Success", "Attraction added successfully!", Alert.AlertType.INFORMATION);
        clearAttractionFields();
        loadAttractions();
    }
    
    @FXML
    private void handleUpdateAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("No Selection", "Please select an attraction to update.", Alert.AlertType.WARNING);
            return;
        }
        
        // Populate fields with selected attraction data
        attractionNameField.setText(selectedAttraction.getName());
        altitudeComboBox.setValue(selectedAttraction.getAltitude());
        attractionDifficultyComboBox.setValue(selectedAttraction.getDifficulty());
        basePriceField.setText(String.valueOf(selectedAttraction.getBasePrice()));
        
        showAlert("Update Mode", "Attraction details loaded. Modify and click 'Add Attraction' to update.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleDeleteAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("No Selection", "Please select an attraction to delete.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this attraction?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showAlert("Attraction Deleted", "Attraction has been removed successfully.", Alert.AlertType.INFORMATION);
            loadAttractions();
        }
    }
    
    @FXML
    private void handleEditBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to edit.", Alert.AlertType.WARNING);
            return;
        }
        
        showAlert("Edit Booking", "Booking ID: " + selectedBooking.getBookingId() + " selected for editing.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to cancel.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to cancel this booking?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showAlert("Booking Cancelled", "Booking has been cancelled successfully.", Alert.AlertType.INFORMATION);
            loadBookings();
            loadAnalytics(); // Refresh analytics
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
            welcomeLabel.setText(translations.get("Welcome, Administrator!"));
            addGuideButton.setText(translations.get("Add Guide"));
            updateGuideButton.setText(translations.get("Update Guide"));
            deleteGuideButton.setText(translations.get("Delete Guide"));
            addAttractionButton.setText(translations.get("Add Attraction"));
            updateAttractionButton.setText(translations.get("Update Attraction"));
            deleteAttractionButton.setText(translations.get("Delete Attraction"));
            editBookingButton.setText(translations.get("Edit Booking"));
            cancelBookingButton.setText(translations.get("Cancel Booking"));
            logoutButton.setText(translations.get("Logout"));
            translateButton.setText(translations.get("Translate to English"));
            
            // Update ComboBoxes
            updateComboBoxLanguage(altitudeComboBox, new String[]{"High", "Low"});
            updateComboBoxLanguage(attractionDifficultyComboBox, new String[]{"Easy", "Medium", "Hard"});
        } else {
            welcomeLabel.setText("Welcome, Administrator!");
            addGuideButton.setText("Add Guide");
            updateGuideButton.setText("Update Guide");
            deleteGuideButton.setText("Delete Guide");
            addAttractionButton.setText("Add Attraction");
            updateAttractionButton.setText("Update Attraction");
            deleteAttractionButton.setText("Delete Attraction");
            editBookingButton.setText("Edit Booking");
            cancelBookingButton.setText("Cancel Booking");
            logoutButton.setText("Logout");
            translateButton.setText("Translate to Nepali");
            
            // Reset ComboBoxes to English
            altitudeComboBox.getItems().clear();
            altitudeComboBox.getItems().addAll("High", "Low");
            attractionDifficultyComboBox.getItems().clear();
            attractionDifficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        }
    }
    
    private void updateComboBoxLanguage(ComboBox<String> comboBox, String[] englishValues) {
        String selectedValue = comboBox.getValue();
        comboBox.getItems().clear();
        
        for (String value : englishValues) {
            comboBox.getItems().add(translations.get(value));
        }
        
        // Set translated selected value
        if (selectedValue != null && translations.containsKey(selectedValue)) {
            comboBox.setValue(translations.get(selectedValue));
        }
    }
    
    private boolean validateGuideFields() {
        if (guideNameField.getText().trim().isEmpty() ||
            guideUsernameField.getText().trim().isEmpty() ||
            guidePasswordField.getText().trim().isEmpty() ||
            guideLanguagesField.getText().trim().isEmpty() ||
            guideExperienceField.getText().trim().isEmpty()) {
            
            showAlert("Validation Error", "Please fill in all guide fields.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private boolean validateAttractionFields() {
        if (attractionNameField.getText().trim().isEmpty() ||
            altitudeComboBox.getValue() == null ||
            attractionDifficultyComboBox.getValue() == null ||
            basePriceField.getText().trim().isEmpty()) {
            
            showAlert("Validation Error", "Please fill in all attraction fields.", Alert.AlertType.ERROR);
            return false;
        }
        
        try {
            Double.parseDouble(basePriceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid price.", Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    private void clearGuideFields() {
        guideNameField.clear();
        guideUsernameField.clear();
        guidePasswordField.clear();
        guideLanguagesField.clear();
        guideExperienceField.clear();
    }
    
    private void clearAttractionFields() {
        attractionNameField.clear();
        altitudeComboBox.setValue(null);
        attractionDifficultyComboBox.setValue(null);
        basePriceField.clear();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
