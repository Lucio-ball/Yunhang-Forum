package com.yunhang.forum;

import com.yunhang.forum.controller.post.PostDetailController;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
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

        // 使用一个示例帖子直接进入详情页，便于测试 PostDetail 界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yunhang/forum/fxml/post/PostDetail.fxml"));
        Parent root = loader.load();
        PostDetailController controller = loader.getController();

        Post sample = PostService.getInstance().getAllPosts().getFirst();
        controller.initData(sample);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/yunhang/forum/css/style.css").toExternalForm());
        stage.setTitle("Yunhang Forum - 帖子详情测试");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
