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

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Button translateButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label roleLabel;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    
    private boolean isNepali = false;
    private Map<String, String> translations = new HashMap<>();
    
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide");
        roleComboBox.setValue("Tourist");
        
        // Show/hide guide-specific fields based on role selection
        roleComboBox.setOnAction(e -> toggleGuideFields());
        toggleGuideFields();
        
        initializeTranslations();
    }
    
    private void initializeTranslations() {
        translations.put("User Registration", "प्रयोगकर्ता दर्ता");
        translations.put("Username:", "प्रयोगकर्ता नाम:");
        translations.put("Password:", "पासवर्ड:");
        translations.put("Full Name:", "पूरा नाम:");
        translations.put("Email:", "इमेल:");
        translations.put("Phone:", "फोन:");
        translations.put("Role:", "भूमिका:");
        translations.put("Languages:", "भाषाहरू:");
        translations.put("Experience:", "अनुभव:");
        translations.put("Register", "दर्ता");
        translations.put("Back to Login", "लगइनमा फर्कनुहोस्");
        translations.put("Translate to Nepali", "नेपालीमा अनुवाद");
        translations.put("Translate to English", "अंग्रेजीमा अनुवाद");
        translations.put("Tourist", "पर्यटक");
        translations.put("Guide", "गाइड");
    }
    
    private void toggleGuideFields() {
        boolean isGuide = "Guide".equals(roleComboBox.getValue()) || 
                         "गाइड".equals(roleComboBox.getValue());
        languagesField.setVisible(isGuide);
        experienceField.setVisible(isGuide);
        languagesLabel.setVisible(isGuide);
        experienceLabel.setVisible(isGuide);
    }
    
    @FXML
    private void handleRegister() {
        if (!validateFields()) {
            return;
        }
        
        String role = roleComboBox.getValue();
        if (isNepali) {
            // Convert Nepali role back to English
            if ("पर्यटक".equals(role)) role = "Tourist";
            else if ("गाइड".equals(role)) role = "Guide";
        }
        
        User user;
        if ("Guide".equals(role)) {
            user = new User(
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                role,
                languagesField.getText().trim(),
                experienceField.getText().trim()
            );
        } else {
            user = new User(
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                role
            );
        }
        
        FileManager.saveUser(user);
        showAlert("Success", "Registration successful! Please login with your credentials.", Alert.AlertType.INFORMATION);
        handleBack();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) backButton.getScene().getWindow();
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
            titleLabel.setText(translations.get("User Registration"));
            usernameLabel.setText(translations.get("Username:"));
            passwordLabel.setText(translations.get("Password:"));
            fullNameLabel.setText(translations.get("Full Name:"));
            emailLabel.setText(translations.get("Email:"));
            phoneLabel.setText(translations.get("Phone:"));
            roleLabel.setText(translations.get("Role:"));
            languagesLabel.setText(translations.get("Languages:"));
            experienceLabel.setText(translations.get("Experience:"));
            registerButton.setText(translations.get("Register"));
            backButton.setText(translations.get("Back to Login"));
            translateButton.setText(translations.get("Translate to English"));
            
            // Update ComboBox
            String selectedRole = roleComboBox.getValue();
            roleComboBox.getItems().clear();
            roleComboBox.getItems().addAll(translations.get("Tourist"), translations.get("Guide"));
            roleComboBox.setValue(translations.get(selectedRole));
        } else {
            titleLabel.setText("User Registration");
            usernameLabel.setText("Username:");
            passwordLabel.setText("Password:");
            fullNameLabel.setText("Full Name:");
            emailLabel.setText("Email:");
            phoneLabel.setText("Phone:");
            roleLabel.setText("Role:");
            languagesLabel.setText("Languages:");
            experienceLabel.setText("Experience:");
            registerButton.setText("Register");
            backButton.setText("Back to Login");
            translateButton.setText("Translate to Nepali");
            
            // Update ComboBox
            String selectedRole = roleComboBox.getValue();
            roleComboBox.getItems().clear();
            roleComboBox.getItems().addAll("Tourist", "Guide");
            
            // Convert back to English
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                if (entry.getValue().equals(selectedRole)) {
                    roleComboBox.setValue(entry.getKey());
                    break;
                }
            }
        }
        toggleGuideFields();
    }
    
    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() ||
            fullNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {
            
            showAlert("Validation Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
            return false;
        }
        
        String role = roleComboBox.getValue();
        if (isNepali && "गाइड".equals(role) || !isNepali && "Guide".equals(role)) {
            if (languagesField.getText().trim().isEmpty() || experienceField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please fill in languages and experience for guides.", Alert.AlertType.ERROR);
                return false;
            }
        }
        
        return true;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
