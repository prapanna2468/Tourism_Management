package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.util.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button translateButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label roleLabel;
    
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide", "Admin");
        roleComboBox.setValue("Tourist");
        initializeTranslations();
    }
    
    private void initializeTranslations() {
        translations.put("Nepal Tourism Management System", "नेपाल पर्यटन व्यवस्थापन प्रणाली");
        translations.put("Username:", "प्रयोगकर्ता नाम:");
        translations.put("Password:", "पासवर्ड:");
        translations.put("Role:", "भूमिका:");
        translations.put("Login", "लगइन");
        translations.put("Register", "दर्ता");
        translations.put("Translate to Nepali", "नेपालीमा अनुवाद");
        translations.put("Translate to English", "अंग्रेजीमा अनुवाद");
        translations.put("Tourist", "पर्यटक");
        translations.put("Guide", "गाइड");
        translations.put("Admin", "प्रशासक");
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }
        
        User user = FileManager.authenticateUser(username, password, role);
        if (user != null) {
            openDashboard(user);
        } else {
            showAlert("Login Failed", "Invalid credentials. Please try again.", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
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
            titleLabel.setText(translations.get("Nepal Tourism Management System"));
            usernameLabel.setText(translations.get("Username:"));
            passwordLabel.setText(translations.get("Password:"));
            roleLabel.setText(translations.get("Role:"));
            loginButton.setText(translations.get("Login"));
            registerButton.setText(translations.get("Register"));
            translateButton.setText(translations.get("Translate to English"));
            
            // Update ComboBox items
            String selectedRole = roleComboBox.getValue();
            roleComboBox.getItems().clear();
            roleComboBox.getItems().addAll(
                translations.get("Tourist"),
                translations.get("Guide"),
                translations.get("Admin")
            );
            roleComboBox.setValue(translations.get(selectedRole));
        } else {
            titleLabel.setText("Nepal Tourism Management System");
            usernameLabel.setText("Username:");
            passwordLabel.setText("Password:");
            roleLabel.setText("Role:");
            loginButton.setText("Login");
            registerButton.setText("Register");
            translateButton.setText("Translate to Nepali");
            
            // Update ComboBox items
            String selectedRole = roleComboBox.getValue();
            roleComboBox.getItems().clear();
            roleComboBox.getItems().addAll("Tourist", "Guide", "Admin");
            
            // Convert back to English
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                if (entry.getValue().equals(selectedRole)) {
                    roleComboBox.setValue(entry.getKey());
                    break;
                }
            }
        }
    }
    
    private void openDashboard(User user) {
        try {
            String fxmlFile = "";
            switch (user.getRole()) {
                case "Tourist":
                    fxmlFile = "/fxml/tourist-dashboard.fxml";
                    break;
                case "Guide":
                    fxmlFile = "/fxml/guide-dashboard.fxml";
                    break;
                case "Admin":
                    fxmlFile = "/fxml/admin-dashboard.fxml";
                    break;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1000, 700);
            
            // Pass user data to controller
            if (user.getRole().equals("Tourist")) {
                TouristDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            } else if (user.getRole().equals("Guide")) {
                GuideDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            } else if (user.getRole().equals("Admin")) {
                AdminDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            }
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Nepal Tourism System - " + user.getRole() + " Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
