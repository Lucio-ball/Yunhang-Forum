package com.yunhang.forum.controller.main;

import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.util.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
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

  // 中间内容区域的 StackPane (Task要求保留，用于默认占位)
  @FXML
  private StackPane contentArea;

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

    // 3. 初始跳转：让界面打开时自动显示首页内容 (可选，但推荐)
    onHomeClicked();
  }

  // --- 导航事件实现 (Task 要求) ---

  @FXML
  public void onHomeClicked() {
    System.out.println("导航: 点击首页 (Home)");
    // Task 要求路径
    ViewManager.loadContent("/com/yunhang/forum/fxml/post/PostList.fxml");
  }

  @FXML
  public void onSquareClicked() {
    System.out.println("导航: 点击广场 (Square)");
    // 广场内容，暂时与首页相同
    ViewManager.loadContent("/com/yunhang/forum/fxml/post/PostList.fxml");
  }

  @FXML
  public void onMyPostsClicked() {
    System.out.println("导航: 点击我的帖子 (My Posts)");
    // Task 要求加载过滤后的列表
    // 假设路径为 MyPostList.fxml
    ViewManager.loadContent("/com/yunhang/forum/fxml/post/MyPostList.fxml");
  }

  @FXML
  public void onSettingsClicked() {
    System.out.println("导航: 点击设置 (Settings)");
    // 假设路径为 Settings.fxml
    ViewManager.loadContent("/com/yunhang/forum/fxml/user/Settings.fxml");
  }
}
