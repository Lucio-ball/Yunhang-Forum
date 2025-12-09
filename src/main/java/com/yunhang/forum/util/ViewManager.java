package com.yunhang.forum.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ViewManager: static utility for switching views in the main window.
 */
public final class ViewManager {
    private static BorderPane mainLayout;
    private static Stage primaryStage;

    private ViewManager() {}

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void loadContent(String fxmlPath) {
        if (mainLayout == null) {
            throw new IllegalStateException("mainLayout is not initialized. Call setMainLayout() first.");
        }
        try {
            Parent content = FXMLLoader.load(ViewManager.class.getResource(fxmlPath));
            mainLayout.setCenter(content);
        } catch (IOException e) {
            // In production, log via a proper logger
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void showLoginWindow() {
        if (primaryStage == null) {
            throw new IllegalStateException("primaryStage is not initialized. Call setPrimaryStage() first.");
        }
        try {
            Parent root = FXMLLoader.load(ViewManager.class.getResource("/com/yunhang/forum/fxml/auth/Login.fxml"));
            Scene scene = new Scene(root, 480, 320);
            scene.getStylesheets().add(ViewManager.class.getResource("/com/yunhang/forum/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to show login window", e);
        }
    }
}

