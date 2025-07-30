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

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TabPane mainTabPane;
    
    // Guide Management
    @FXML private TextField guideNameField;
    @FXML private TextField guideUsernameField;
    @FXML private PasswordField guidePasswordField;
    @FXML private TextField guideLanguagesField;
    @FXML private TextField guideExperienceField;
    @FXML private Button addGuideButton;
    @FXML private Button updateGuideButton;
    @FXML private Button deleteGuideButton;
    @FXML private TableView<User> guidesTable;
    @FXML private TableColumn<User, String> guideNameColumn;
    @FXML private TableColumn<User, String> guideUsernameColumn;
    @FXML private TableColumn<User, String> guideLanguagesColumn;
    @FXML private TableColumn<User, String> guideExperienceColumn;
    
    // Attraction Management
    @FXML private TextField attractionNameField;
    @FXML private ComboBox<String> altitudeComboBox;
    @FXML private ComboBox<String> attractionDifficultyComboBox;
    @FXML private TextField basePriceField;
    @FXML private Button addAttractionButton;
    @FXML private Button updateAttractionButton;
    @FXML private Button deleteAttractionButton;
    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> attractionNameColumn;
    @FXML private TableColumn<Attraction, String> altitudeColumn;
    @FXML private TableColumn<Attraction, String> attractionDifficultyColumn;
    @FXML private TableColumn<Attraction, Double> basePriceColumn;
    
    // Booking Management
    @FXML private Button editBookingButton;
    @FXML private Button cancelBookingButton;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> bookingAttractionColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    
    // Analytics
    @FXML private PieChart nationalityChart;
    @FXML private BarChart<String, Number> popularAttractionsChart;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label messageLabel;
    
    @FXML private Button translateButton;
    @FXML private Button logoutButton;
    
    private User currentUser;
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome Admin, " + user.getFullName());
        loadAllData();
    }
    
    @FXML
    private void initialize() {
        initializeTranslations();
        setupTableColumns();
        setupComboBoxes();
    }
    
    private void initializeTranslations() {
        translations.put("Welcome Admin, ", "स्वागत प्रशासक, ");
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
        translations.put("Medium", "मध्यम");
        translations.put("Easy", "सजिलो");
        translations.put("Hard", "कठिन");
    }
    
    private void setupTableColumns() {
        // Guide table columns
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        guideUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        guideLanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        guideExperienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
        
        // Attraction table columns
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
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        // Booking table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristColumn.setCellValueFactory(new PropertyValueFactory<>("tourist"));
        bookingAttractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    private void setupComboBoxes() {
        altitudeComboBox.getItems().addAll("Low", "Medium", "High");
        altitudeComboBox.setValue("Low");
        
        attractionDifficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        attractionDifficultyComboBox.setValue("Easy");
    }
    
    private void loadAllData() {
        loadGuides();
        loadAttractions();
        loadBookings();
        loadAnalytics();
    }
    
    private void loadGuides() {
        // For simplicity, we'll create some sample guides
        ObservableList<User> guides = FXCollections.observableArrayList();
        
        User guide1 = new User();
        guide1.setFullName("Ram Sharma");
        guide1.setUsername("ram_guide");
        guide1.setLanguages("English, Nepali, Hindi");
        guide1.setExperience("8 years");
        guides.add(guide1);
        
        User guide2 = new User();
        guide2.setFullName("Sita Gurung");
        guide2.setUsername("sita_guide");
        guide2.setLanguages("English, Nepali");
        guide2.setExperience("5 years");
        guides.add(guide2);
        
        guidesTable.setItems(guides);
    }
    
    private void loadAttractions() {
        List<Attraction> attractions = FileManager.getAttractions();
        ObservableList<Attraction> attractionsList = FXCollections.observableArrayList(attractions);
        attractionsTable.setItems(attractionsList);
    }
    
    private void loadBookings() {
        List<Booking> bookings = FileManager.getBookings();
        ObservableList<Booking> bookingsList = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(bookingsList);
    }
    
    private void loadAnalytics() {
        // Load nationality distribution (sample data)
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Nepal", 35),
            new PieChart.Data("India", 25),
            new PieChart.Data("USA", 15),
            new PieChart.Data("UK", 10),
            new PieChart.Data("Others", 15)
        );
        nationalityChart.setData(pieChartData);
        nationalityChart.setTitle("Tourist Nationality Distribution");
        
        // Load popular attractions chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");
        series.getData().add(new XYChart.Data<>("Everest Base Camp", 45));
        series.getData().add(new XYChart.Data<>("Annapurna Circuit", 38));
        series.getData().add(new XYChart.Data<>("Chitwan National Park", 32));
        series.getData().add(new XYChart.Data<>("Pokhara Lake", 28));
        series.getData().add(new XYChart.Data<>("Langtang Valley", 25));
        
        popularAttractionsChart.getData().clear();
        popularAttractionsChart.getData().add(series);
        popularAttractionsChart.setTitle("Most Popular Attractions");
        
        // Calculate total revenue
        List<Booking> bookings = FileManager.getBookings();
        double totalRevenue = bookings.stream().mapToDouble(Booking::getPrice).sum();
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
    }
    
    @FXML
    private void handleAddGuide() {
        if (!validateGuideFields()) {
            return;
        }
        
        User guide = new User();
        guide.setFullName(guideNameField.getText().trim());
        guide.setUsername(guideUsernameField.getText().trim());
        guide.setPassword(User.hashPassword(guidePasswordField.getText()));
        guide.setLanguages(guideLanguagesField.getText().trim());
        guide.setExperience(guideExperienceField.getText().trim());
        guide.setRole("Guide");
        
        FileManager.saveUser(guide);
        loadGuides();
        clearGuideFields();
        
        showAlert("Success", "Guide added successfully!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleUpdateGuide() {
        User selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("Error", "Please select a guide to update.", Alert.AlertType.ERROR);
            return;
        }
        
        showAlert("Update Guide", "Guide update functionality would be implemented here.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleDeleteGuide() {
        User selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("Error", "Please select a guide to delete.", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Guide");
        confirmAlert.setContentText("Are you sure you want to delete this guide?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            guidesTable.getItems().remove(selectedGuide);
            showAlert("Success", "Guide deleted successfully!", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleAddAttraction() {
        String name = attractionNameField.getText().trim();
        String altitude = altitudeComboBox.getValue();
        String difficulty = attractionDifficultyComboBox.getValue();
        String priceText = basePriceField.getText().trim();

        if (name.isEmpty() || altitude == null || difficulty == null || priceText.isEmpty()) {
            messageLabel.setText(isNepali ? "कृपया सबै फिल्डहरू भर्नुहोस्" : "Please fill all fields");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Attraction attraction = new Attraction(name, altitude, difficulty, price);
            FileManager.saveAttraction(attraction);
            
            messageLabel.setText(isNepali ? "आकर्षण सफलतापूर्वक थपियो" : "Attraction added successfully");
            
            // Clear fields
            attractionNameField.clear();
            altitudeComboBox.setValue(null);
            attractionDifficultyComboBox.setValue(null);
            basePriceField.clear();
            
        } catch (NumberFormatException e) {
            messageLabel.setText(isNepali ? "अवैध मूल्य ढाँचा" : "Invalid price format");
        }
    }
    
    @FXML
    private void handleUpdateAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("Error", "Please select an attraction to update.", Alert.AlertType.ERROR);
            return;
        }
        
        showAlert("Update Attraction", "Attraction update functionality would be implemented here.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleDeleteAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("Error", "Please select an attraction to delete.", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Attraction");
        confirmAlert.setContentText("Are you sure you want to delete this attraction?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            attractionsTable.getItems().remove(selectedAttraction);
            showAlert("Success", "Attraction deleted successfully!", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleEditBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to edit.", Alert.AlertType.ERROR);
            return;
        }
        
        showAlert("Edit Booking", "Booking edit functionality would be implemented here.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to cancel.", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Booking");
        confirmAlert.setContentText("Are you sure you want to cancel this booking?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            selectedBooking.setStatus("Cancelled");
            bookingsTable.refresh();
            showAlert("Success", "Booking cancelled successfully!", Alert.AlertType.INFORMATION);
        }
    }
    
    private boolean validateGuideFields() {
        if (guideNameField.getText().trim().isEmpty() ||
            guideUsernameField.getText().trim().isEmpty() ||
            guidePasswordField.getText().isEmpty() ||
            guideLanguagesField.getText().trim().isEmpty() ||
            guideExperienceField.getText().trim().isEmpty()) {
            
            showAlert("Error", "Please fill in all guide fields.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private boolean validateAttractionFields() {
        if (attractionNameField.getText().trim().isEmpty() ||
            altitudeComboBox.getValue() == null ||
            attractionDifficultyComboBox.getValue() == null ||
            basePriceField.getText().trim().isEmpty()) {
            
            showAlert("Error", "Please fill in all attraction fields.", Alert.AlertType.ERROR);
            return false;
        }
        
        try {
            Double.parseDouble(basePriceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid price.", Alert.AlertType.ERROR);
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
        altitudeComboBox.setValue("Low");
        attractionDifficultyComboBox.setValue("Easy");
        basePriceField.clear();
    }
    
    @FXML
    private void handleTranslate() {
        isNepali = !isNepali;
        updateLanguage();
    }
    
    private void updateLanguage() {
        if (isNepali) {
            welcomeLabel.setText(translations.get("Welcome Admin, ") + currentUser.getFullName() + "!");
            translateButton.setText(translations.get("Translate to English"));
            logoutButton.setText(translations.get("Logout"));
            
            // Update buttons
            addGuideButton.setText(translations.get("Add Guide"));
            updateGuideButton.setText(translations.get("Update Guide"));
            deleteGuideButton.setText(translations.get("Delete Guide"));
            addAttractionButton.setText(translations.get("Add Attraction"));
            updateAttractionButton.setText(translations.get("Update Attraction"));
            deleteAttractionButton.setText(translations.get("Delete Attraction"));
            editBookingButton.setText(translations.get("Edit Booking"));
            cancelBookingButton.setText(translations.get("Cancel Booking"));
            
            // Update ComboBoxes
            String selectedAltitude = altitudeComboBox.getValue();
            altitudeComboBox.getItems().clear();
            altitudeComboBox.getItems().addAll(translations.get("Low"), translations.get("Medium"), translations.get("High"));
            altitudeComboBox.setValue(translations.get(selectedAltitude));
            
            String selectedDifficulty = attractionDifficultyComboBox.getValue();
            attractionDifficultyComboBox.getItems().clear();
            attractionDifficultyComboBox.getItems().addAll(
                translations.get("Easy"),
                translations.get("Medium"),
                translations.get("Hard")
            );
            attractionDifficultyComboBox.setValue(translations.get(selectedDifficulty));
        } else {
            welcomeLabel.setText("Welcome Admin, " + currentUser.getFullName() + "!");
            translateButton.setText(translations.get("Translate to Nepali"));
            logoutButton.setText("Logout");
            
            // Restore English buttons
            addGuideButton.setText("Add Guide");
            updateGuideButton.setText("Update Guide");
            deleteGuideButton.setText("Delete Guide");
            addAttractionButton.setText("Add Attraction");
            updateAttractionButton.setText("Update Attraction");
            deleteAttractionButton.setText("Delete Attraction");
            editBookingButton.setText("Edit Booking");
            cancelBookingButton.setText("Cancel Booking");
            
            // Restore English ComboBoxes
            setupComboBoxes();
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
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void loadAllBookings() {
        List<Booking> bookings = FileManager.getBookings();
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(bookingList);
    }
    
    private void updateStatistics() {
        List<Booking> bookings = FileManager.getBookings();
        int totalBookings = bookings.size();
        double totalRevenue = bookings.stream().mapToDouble(Booking::getPrice).sum();

        totalBookingsLabel.setText((isNepali ? "कुल बुकिङहरू: " : "Total Bookings: ") + totalBookings);
        totalRevenueLabel.setText((isNepali ? "कुल राजस्व: $" : "Total Revenue: $") + 
                                String.format("%.2f", totalRevenue));
    }
}
