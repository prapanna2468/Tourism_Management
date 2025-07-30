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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label earningsLabel;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    @FXML private TableView<Booking> treksTable;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> trekColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> difficultyColumn;
    @FXML private TextArea updatesArea;
    @FXML private Button logoutButton;
    @FXML private Button translateButton;
    
    private User currentUser;
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeInfo();
        loadUpcomingTreks();
        loadImportantUpdates();
    }
    
    @FXML
    private void initialize() {
        initializeTranslations();
        setupTable();
    }
    
    private void initializeTranslations() {
        translations.put("Welcome,", "स्वागत छ,");
        translations.put("Earnings:", "आम्दानी:");
        translations.put("Languages:", "भाषाहरू:");
        translations.put("Experience:", "अनुभव:");
        translations.put("Logout", "लगआउट");
        translations.put("Translate to Nepali", "नेपालीमा अनुवाद");
        translations.put("Translate to English", "अंग्रेजीमा अनुवाद");
    }
    
    private void setupTable() {
        touristColumn.setCellValueFactory(new PropertyValueFactory<>("tourist"));
        trekColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
    }
    
    private void updateWelcomeInfo() {
        if (currentUser == null) return;
        
        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        
        // Calculate earnings (10-15% commission)
        List<Booking> allBookings = FileManager.getBookings();
        double totalEarnings = allBookings.stream()
            .filter(booking -> currentUser.getUsername().equals(booking.getGuide()))
            .mapToDouble(booking -> booking.getPrice() * 0.12) // 12% commission
            .sum();
        
        earningsLabel.setText("Earnings: $" + String.format("%.2f", totalEarnings));
        languagesLabel.setText("Languages: " + (currentUser.getLanguages() != null ? currentUser.getLanguages() : "English, Nepali"));
        experienceLabel.setText("Experience: " + (currentUser.getExperience() != null ? currentUser.getExperience() : "5 years"));
    }
    
    private void loadUpcomingTreks() {
        if (currentUser == null) return;
        
        List<Booking> allBookings = FileManager.getBookings();
        List<Booking> guideTreks = allBookings.stream()
            .filter(booking -> currentUser.getUsername().equals(booking.getGuide()))
            .filter(booking -> booking.getDate().isAfter(LocalDate.now()) || booking.getDate().isEqual(LocalDate.now()))
            .collect(Collectors.toList());
        
        ObservableList<Booking> trekList = FXCollections.observableArrayList(guideTreks);
        treksTable.setItems(trekList);
    }
    
    private void loadImportantUpdates() {
        StringBuilder updates = new StringBuilder();
        updates.append("🌨️ WEATHER ALERT: Heavy snow expected on Everest Base Camp trek from Dec 15-20. Please prepare accordingly.\n\n");
        updates.append("⚠️ SAFETY NOTICE: All high-altitude treks require mandatory health checkups. Please ensure tourists have proper documentation.\n\n");
        updates.append("📢 EMERGENCY CONTACT: In case of emergencies, contact Nepal Tourism Board at +977-1-4256909.\n\n");
        updates.append("🏔️ ROUTE UPDATE: Alternative route available for Annapurna Circuit due to landslide on main trail.\n\n");
        updates.append("💰 COMMISSION UPDATE: Guide commission rates have been updated to 12-15% based on trek difficulty.\n\n");
        updates.append("🎉 FESTIVAL SEASON: 20% discount period active for Dashain & Tihar (Aug-Oct). Expect higher booking volumes.");
        
        updatesArea.setText(updates.toString());
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
                
                // Update earnings label
                String earningsText = earningsLabel.getText();
                String amount = earningsText.substring(earningsText.indexOf("$"));
                earningsLabel.setText(translations.get("Earnings:") + " " + amount);
                
                // Update other labels
                String languages = currentUser.getLanguages() != null ? currentUser.getLanguages() : "English, Nepali";
                languagesLabel.setText(translations.get("Languages:") + " " + languages);
                
                String experience = currentUser.getExperience() != null ? currentUser.getExperience() : "5 years";
                experienceLabel.setText(translations.get("Experience:") + " " + experience);
            }
            
            logoutButton.setText(translations.get("Logout"));
            translateButton.setText(translations.get("Translate to English"));
            
            // Translate updates
            String nepaliUpdates = "🌨️ मौसम चेतावनी: डिसेम्बर १५-२० सम्म एभरेस्ट बेस क्याम्प ट्रेकमा भारी हिउँ पर्ने अपेक्षा। कृपया तदनुसार तयारी गर्नुहोस्।\n\n" +
                                 "⚠️ सुरक्षा सूचना: सबै उच्च उचाइका ट्रेकहरूका लागि अनिवार्य स्वास्थ्य जाँच आवश्यक। कृपया पर्यटकहरूसँग उचित कागजातहरू छन् भनी सुनिश्चित गर्नुहोस्।\n\n" +
                                 "📢 आपतकालीन सम्पर्क: आपतकालीन अवस्थामा, नेपाल पर्यटन बोर्डलाई +977-1-4256909 मा सम्पर्क गर्नुहोस्।\n\n" +
                                 "🏔️ मार्ग अपडेट: मुख्य बाटोमा पहिरोका कारण अन्नपूर्ण सर्किटका लागि वैकल्पिक मार्ग उपलब्ध।\n\n" +
                                 "💰 कमिसन अपडेट: ट्रेकको कठिनाईका आधारमा गाइड कमिसन दर १२-१५% मा अपडेट गरिएको छ।\n\n" +
                                 "🎉 चाडपर्वको मौसम: दशैं र तिहारका लागि २०% छुट अवधि सक्रिय (अगस्त-अक्टोबर)। बढी बुकिङको अपेक्षा गर्नुहोस्।";
            updatesArea.setText(nepaliUpdates);
        } else {
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
                updateWelcomeInfo(); // Reset to English
            }
            
            logoutButton.setText("Logout");
            translateButton.setText("Translate to Nepali");
            loadImportantUpdates(); // Reset to English updates
        }
    }
}
