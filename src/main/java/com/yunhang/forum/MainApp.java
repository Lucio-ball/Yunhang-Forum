package com.yunhang.forum;

import com.yunhang.forum.controller.post.PostDetailController;
import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.dao.impl.FileDataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.AppContext;
import com.yunhang.forum.util.ResourcePaths;
import com.yunhang.forum.util.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    private static final boolean DEBUG_MODE_POST_DETAIL = false;

    @Override
    public void start(Stage stage) throws Exception {
        // Composition root: wire DAO implementation once
        DataLoader dataLoader = new FileDataLoader();
        AppContext.setDataLoader(dataLoader);

        // 1. 初始化 ViewManager 并设置主舞台
        ViewManager.setPrimaryStage(stage);

        if (DEBUG_MODE_POST_DETAIL) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(ResourcePaths.FXML_POST_DETAIL));
            Parent root = loader.load();
            PostDetailController controller = loader.getController();

            // 假设 PostService 实例和 getAllPosts 方法可用
            Post sample = PostService.getInstance().getAllPosts().getFirst();
            controller.initData(sample);

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource(ResourcePaths.CSS_STYLE).toExternalForm());
            stage.setTitle("Yunhang Forum - 帖子详情测试 [DEBUG]");
            stage.setScene(scene);
            stage.show();

        } else {
            ViewManager.switchScene(ResourcePaths.FXML_AUTH_LOGIN);
            stage.setTitle("Yunhang Forum - 用户登录");
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
