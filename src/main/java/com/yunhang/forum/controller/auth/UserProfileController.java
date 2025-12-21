package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ResourcePaths;
import com.yunhang.forum.util.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class UserProfileController {

    // FXML 元素
    @FXML private Label welcomeLabel;
    @FXML private Label studentIdLabel;
    @FXML private ListView<String> postListView;

    private final UserService userService = new UserService();

    @FXML
    protected void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser != null) {
            // 1. 显示用户基本信息
            welcomeLabel.setText("欢迎，" + currentUser.getNickname() + "!");
            studentIdLabel.setText("学号: " + currentUser.getStudentID());
            loadUserPosts(currentUser);
        } else {
            ViewManager.switchScene(ResourcePaths.FXML_AUTH_LOGIN);
        }
    }

    private void loadUserPosts(User user) {
        List<Post> posts = userService.getUserPosts(user.getStudentID());

        if (posts != null && !posts.isEmpty()) {
            for (Post post : posts) {
                postListView.getItems().add(post.getTitle() + " (" + post.getPublishTime() + ")");
            }
        } else {
            postListView.getItems().add("您还没有发布任何帖子‍哦，快发个新帖一起玩吧😋");
        }
    }
    @FXML
    protected void handleLogoutAction() {
        UserSession.getInstance().endSession();
        ViewManager.switchScene(ResourcePaths.FXML_AUTH_LOGIN);
    }
    @FXML
    private void handleGoToForum() {
        ViewManager.switchScene(ResourcePaths.FXML_AUTH_POST_LIST);
    }
    @FXML
    private void handleNewPost() {
        ViewManager.switchScene(ResourcePaths.FXML_POST_DETAIL);
    }
    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("注销确认");
        alert.setHeaderText("您确定要注销账号吗？");
        alert.setContentText("注销后账号无法恢复。");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserSession.getInstance().endSession();
                ViewManager.switchScene(ResourcePaths.FXML_AUTH_LOGIN);
            }
        });
    }
}
