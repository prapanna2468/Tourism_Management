package com.tourism;

import com.tourism.util.FileManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TourismApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data files
        FileManager.initializeFiles();
        
        // Load login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        
        primaryStage.setTitle("Nepal Tourism Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

