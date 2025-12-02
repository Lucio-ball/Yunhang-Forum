package com.yunhang.forum;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Yunhang Forum desktop application entry point.
 * <p>
 * This minimal JavaFX launcher will be expanded later with FXML and controllers.
 */
public class YunhangForumApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Basic placeholder UI for initial bootstrapping
        Label label = new Label("Yunhang Forum - JavaFX Initialized");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Yunhang Forum");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Standard main method to launch JavaFX app.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
