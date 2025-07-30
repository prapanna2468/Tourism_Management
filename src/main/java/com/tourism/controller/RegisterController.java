package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.util.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private Label messageLabel;
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
    @FXML private Button registerButton;
    @FXML private Button backButton;

    private boolean isNepali = false;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide");
        roleComboBox.setValue("Tourist");
        
        // Show/hide guide-specific fields based on role selection
        roleComboBox.setOnAction(e -> {
            boolean isGuide = "Guide".equals(roleComboBox.getValue());
            languagesField.setVisible(isGuide);
            experienceField.setVisible(isGuide);
            languagesLabel.setVisible(isGuide);
            experienceLabel.setVisible(isGuide);
        });
        
        // Initially hide guide fields
        languagesField.setVisible(false);
        experienceField.setVisible(false);
        languagesLabel.setVisible(false);
        experienceLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || 
            email.isEmpty() || phone.isEmpty()) {
            messageLabel.setText(isNepali ? "कृपया सबै आवश्यक फिल्डहरू भर्नुहोस्" : "Please fill all required fields");
            return;
        }

        User user;
        if ("Guide".equals(role)) {
            String languages = languagesField.getText().trim();
            String experience = experienceField.getText().trim();
            if (languages.isEmpty() || experience.isEmpty()) {
                messageLabel.setText(isNepali ? "गाइडका लागि भाषा र अनुभव आवश्यक छ" : "Languages and experience required for guides");
                return;
            }
            user = new User(username, password, fullName, email, phone, role, languages, experience);
        } else {
            user = new User(username, password, fullName, email, phone, role);
        }

        try {
            FileManager.saveUser(user);
            messageLabel.setText(isNepali ? "सफल दर्ता! कृपया लगइन गर्नुहोस्" : "Registration successful! Please login");
            
            // Clear fields
            clearFields();
        } catch (Exception e) {
            messageLabel.setText(isNepali ? "दर्ता त्रुटि: " + e.getMessage() : "Registration error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTranslate() {
        isNepali = !isNepali;
        if (isNepali) {
            titleLabel.setText("नयाँ खाता दर्ता गर्नुहोस्");
            usernameLabel.setText("प्रयोगकर्ता नाम:");
            passwordLabel.setText("पासवर्ड:");
            fullNameLabel.setText("पूरा नाम:");
            emailLabel.setText("इमेल:");
            phoneLabel.setText("फोन:");
            roleLabel.setText("भूमिका:");
            languagesLabel.setText("भाषाहरू:");
            experienceLabel.setText("अनुभव:");
            registerButton.setText("दर्ता गर्नुहोस्");
            backButton.setText("फिर्ता");
            translateButton.setText("अंग्रेजीमा अनुवाद गर्नुहोस्");
        } else {
            titleLabel.setText("Register New Account");
            usernameLabel.setText("Username:");
            passwordLabel.setText("Password:");
            fullNameLabel.setText("Full Name:");
            emailLabel.setText("Email:");
            phoneLabel.setText("Phone:");
            roleLabel.setText("Role:");
            languagesLabel.setText("Languages:");
            experienceLabel.setText("Experience:");
            registerButton.setText("Register");
            backButton.setText("Back");
            translateButton.setText("Translate to Nepali");
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        languagesField.clear();
        experienceField.clear();
        roleComboBox.setValue("Tourist");
    }
}
