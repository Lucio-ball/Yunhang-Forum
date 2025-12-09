package com.yunhang.forum;

import com.yunhang.forum.util.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for Yunhang Forum.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ViewManager.setPrimaryStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("/com/yunhang/forum/fxml/auth/Login.fxml"));
        Scene scene = new Scene(root, 480, 320);
        scene.getStylesheets().add(getClass().getResource("/com/yunhang/forum/css/style.css").toExternalForm());
        stage.setTitle("Yunhang Forum");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
