package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.util.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label messageLabel;
    @FXML private Button translateButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label roleLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    private boolean isNepali = false;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide", "Admin");
        roleComboBox.setValue("Tourist");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText(isNepali ? "कृपया सबै फिल्डहरू भर्नुहोस्" : "Please fill all fields");
            return;
        }

        User user = FileManager.authenticateUser(username, password, role);
        if (user != null) {
            messageLabel.setText(isNepali ? "सफल लगइन!" : "Login successful!");
            openDashboard(user);
        } else {
            messageLabel.setText(isNepali ? "अवैध प्रमाणहरू" : "Invalid credentials");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
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
            titleLabel.setText("नेपाल पर्यटन व्यवस्थापन प्रणाली");
            usernameLabel.setText("प्रयोगकर्ता नाम:");
            passwordLabel.setText("पासवर्ड:");
            roleLabel.setText("भूमिका:");
            loginButton.setText("लगइन");
            registerButton.setText("दर्ता गर्नुहोस्");
            translateButton.setText("अंग्रेजीमा अनुवाद गर्नुहोस्");
        } else {
            titleLabel.setText("Nepal Tourism Management System");
            usernameLabel.setText("Username:");
            passwordLabel.setText("Password:");
            roleLabel.setText("Role:");
            loginButton.setText("Login");
            registerButton.setText("Register");
            translateButton.setText("Translate to Nepali");
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
            
            // Pass user data to dashboard controller
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

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error opening dashboard: " + e.getMessage());
        }
    }
}
