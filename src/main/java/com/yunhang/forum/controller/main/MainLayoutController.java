package com.yunhang.forum.controller.main;

import com.yunhang.forum.controller.post.PostEditorController;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 主界面控制器
 */
public class MainLayoutController implements Initializable {

  // 根节点 BorderPane，用于传给 ViewManager.setMainLayout()
  @FXML
  private BorderPane mainRoot;

  // Header 区域的组件
  @FXML
  private Label nicknameLabel;

  @FXML
  private ImageView avatarImageView;

  // 【新增】搜索框组件
  @FXML
  private TextField searchTextField;

  // 中间内容区域的 StackPane (Task要求保留，用于默认占位)
  @FXML
  private StackPane contentArea;

  // 发帖按钮 - 在FXML中添加这个按钮并设置fx:id="publishButton"
  @FXML
  private Button publishButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    System.out.println("MainLayoutController 初始化中...");

    // 1. 【最关键一步】将整个 BorderPane 传给 ViewManager (符合 ViewManager 接口)
    if (mainRoot != null) {
      ViewManager.setMainLayout(mainRoot);
    } else {
      System.err.println("错误: FXML根节点的fx:id='mainRoot'未加载。");
    }

    // 2. 初始化用户信息 (Task 要求)
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser != null) {
      nicknameLabel.setText(currentUser.getNickname());
      // TODO: 设置头像的逻辑
    } else {
      nicknameLabel.setText("未登录");
    }

    // 3. 【新增】绑定发帖按钮点击事件
    if (publishButton != null) {
      publishButton.setOnAction(event -> onPublishClicked());
    } else {
      System.err.println("警告: 发帖按钮未找到，请在FXML中添加fx:id='publishButton'");
    }

    // 4. 初始跳转：让界面打开时自动显示首页内容 (可选，但推荐)
    onHomeClicked();
  }

  // --- 发帖按钮绑定（新增方法）---
  /**
   * 点击发帖按钮 - 打开发帖弹窗
   */
  @FXML
  public void onPublishClicked() {
    System.out.println("发帖按钮被点击");

    // 检查用户是否登录
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("未登录");
      alert.setHeaderText(null);
      alert.setContentText("请先登录后再发帖");
      alert.showAndWait();
      return;
    }

    try {
      // 加载发帖编辑器FXML
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/com/yunhang/forum/fxml/post/PostEditor.fxml")
      );
      Parent editorRoot = loader.load();

      // 获取控制器
      PostEditorController editorController = loader.getController();

      // 创建弹窗
      Stage dialogStage = new Stage();
      dialogStage.setTitle("发布新帖子");
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initStyle(StageStyle.UTILITY);
      dialogStage.setScene(new Scene(editorRoot));

      // 设置弹窗大小
      dialogStage.setMinWidth(500);
      dialogStage.setMinHeight(450);

      // 配置控制器
      editorController.setDialogStage(dialogStage);

      // 设置发布成功回调
      editorController.setOnPublishSuccess(() -> {
        // 发布成功后，可以在这里添加刷新逻辑
        // 例如：刷新帖子列表或显示成功消息
        System.out.println("帖子发布成功，可以刷新列表");
      });

      // 显示弹窗
      dialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("错误");
      alert.setHeaderText(null);
      alert.setContentText("无法加载发帖界面");
      alert.showAndWait();
    }
  }

  // --- 原有的导航事件实现 (保持不变) ---
  /**
   * 触发: 获取关键词 -> 调用 PostService.searchPosts -> 跳转/刷新显示结果
   */
  @FXML
  public void onSearchEnter() {
    String keyword = searchTextField.getText();
    if (keyword == null || keyword.trim().isEmpty()) {
      return;
    }

    // 使用多线程防止文件 IO 导致界面卡顿
    new Thread(() -> {
      // 1. 调用 Service 层进行关键词搜索
      List<Post> results = PostService.getInstance().searchPosts(keyword);

      // 2. 切换回 UI 线程进行跳转和内容更新
      Platform.runLater(() -> {
        // 跳转到帖子列表页面
        ViewManager.loadContent("auth/PostList.fxml");

        // 打印日志以便联调
        System.out.println("搜索触发，关键词: " + keyword + "，找到结果数: " + results.size());
      });
    }).start();
  }

  // --- 导航事件实现 (Task 要求) ---
  @FXML
  public void onHomeClicked() {
    System.out.println("导航: 点击首页 (Home)");
    // 使用 ViewManager 的相对路径（避免重复拼接 /com/yunhang/forum/fxml/ 前缀）
    ViewManager.loadContent("auth/PostList.fxml");
  }

  @FXML
  public void onSquareClicked() {
    System.out.println("导航: 点击广场 (Square)");
    // 广场内容，暂时与首页相同
    ViewManager.loadContent("auth/PostList.fxml");
  }

  @FXML
  public void onMyPostsClicked() {
    System.out.println("导航: 点击我的帖子 (My Posts)");
    // Phase 2：复用用户中心页，展示"我的帖子"列表
    ViewManager.loadContent("auth/UserProfile.fxml");
  }

  @FXML
  public void onSettingsClicked() {
    System.out.println("导航: 点击设置 (Settings)");
    ViewManager.loadContent("user/Settings.fxml");
  }
}
