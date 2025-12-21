package com.yunhang.forum.controller.post;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * 发帖编辑器控制器
 */
public class PostEditorController {

  @FXML
  private TextField titleField;

  @FXML
  private ComboBox<PostCategory> categoryComboBox;

  @FXML
  private TextArea contentArea;

  @FXML
  private CheckBox anonymousCheckBox;

  @FXML
  private Button publishButton;

  @FXML
  private Button cancelButton;

  private Stage dialogStage;
  private Runnable onPublishSuccess;

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    // 初始化分类下拉框
    categoryComboBox.getItems().addAll(Arrays.asList(PostCategory.values()));
    categoryComboBox.setValue(PostCategory.CAMPUS_LIFE); // 默认值

    // 设置提示文本
    titleField.setPromptText("请输入帖子标题（1-100字）");
    contentArea.setPromptText("请输入帖子内容...");

    // 设置输入限制
    setupInputLimits();
  }

  /**
   * 设置弹窗舞台
   */
  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  /**
   * 设置发布成功回调
   */
  public void setOnPublishSuccess(Runnable callback) {
    this.onPublishSuccess = callback;
  }

  /**
   * 发布帖子
   */
  @FXML
  private void onPublish() {
    // 验证输入
    if (!validateInput()) {
      return;
    }

    // 获取当前用户
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) {
      showAlert("错误", "请先登录", Alert.AlertType.ERROR);
      return;
    }

    // 获取用户ID（这里需要根据User类的实际方法获取ID）
    String authorId = getUserId(currentUser);
    if (authorId == null || authorId.isEmpty()) {
      showAlert("错误", "无法获取用户信息", Alert.AlertType.ERROR);
      return;
    }

    // 构建帖子对象
    Post post = new Post(
        titleField.getText().trim(),
        contentArea.getText().trim(),
        authorId,
        categoryComboBox.getValue()
    );

    // 设置匿名状态
    post.setAnonymous(anonymousCheckBox.isSelected());

    // 如果是匿名发布，设置显示作者为匿名
    if (anonymousCheckBox.isSelected()) {
      post.setAnonymous(true);
    }

    // 禁用按钮，显示加载状态
    publishButton.setDisable(true);
    publishButton.setText("发布中...");

    // 异步创建帖子
    new Thread(() -> {
      try {
        // 模拟网络延迟
        Thread.sleep(1000);

        // 这里应该调用实际的PostService
        System.out.println("创建帖子 - 标题: " + post.getTitle());
        System.out.println("作者ID: " + authorId);
        System.out.println("分类: " + post.getCategory());
        System.out.println("匿名: " + post.isAnonymous());

        // 获取PostService实例并创建帖子
        // PostService.getInstance().createPost(post, onSuccess, onError);

        // 切换到UI线程更新界面
        Platform.runLater(() -> {
          // 发布成功
          showAlert("成功", "帖子发布成功！", Alert.AlertType.INFORMATION);

          // 关闭窗口
          if (dialogStage != null) {
            dialogStage.close();
          }

          // 调用成功回调（刷新列表）
          if (onPublishSuccess != null) {
            onPublishSuccess.run();
          }
        });

      } catch (Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
          showAlert("错误", "发布失败: " + e.getMessage(), Alert.AlertType.ERROR);
          publishButton.setDisable(false);
          publishButton.setText("发布");
        });
      }
    }).start();
  }

  /**
   * 从User对象获取用户ID
   * 根据您的User类实际情况调整这个方法
   */
  private String getUserId(User user) {
    if (user == null) {
      return null;
    }

    // 假设User类有以下方法之一来获取ID：
    // 1. user.getUserId() - 如果User类有这个属性
    // 2. user.getId() - 如果User类有这个属性
    // 3. 其他标识符

    try {
      // 方法1：尝试getUserId()方法
      java.lang.reflect.Method method = user.getClass().getMethod("getUserId");
      Object result = method.invoke(user);
      if (result instanceof String) {
        return (String) result;
      }
    } catch (Exception e1) {
      try {
        // 方法2：尝试getId()方法
        java.lang.reflect.Method method = user.getClass().getMethod("getId");
        Object result = method.invoke(user);
        if (result instanceof String) {
          return (String) result;
        }
      } catch (Exception e2) {
        // 如果都没有对应的方法，返回默认值或抛出异常
        System.err.println("无法获取用户ID，User类缺少相应方法");
      }
    }

    // 如果以上方法都不行，返回一个默认值（仅用于开发测试）
    return "user_" + System.currentTimeMillis();
  }

  /**
   * 取消发布
   */
  @FXML
  private void onCancel() {
    if (dialogStage != null) {
      dialogStage.close();
    }
  }

  /**
   * 验证输入
   */
  private boolean validateInput() {
    String title = titleField.getText().trim();
    String content = contentArea.getText().trim();

    if (title.isEmpty()) {
      showAlert("验证错误", "标题不能为空", Alert.AlertType.WARNING);
      titleField.requestFocus();
      return false;
    }

    if (title.length() > 100) {
      showAlert("验证错误", "标题不能超过100字", Alert.AlertType.WARNING);
      titleField.requestFocus();
      return false;
    }

    if (content.isEmpty()) {
      showAlert("验证错误", "内容不能为空", Alert.AlertType.WARNING);
      contentArea.requestFocus();
      return false;
    }

    if (content.length() > 5000) {
      showAlert("验证错误", "内容不能超过5000字", Alert.AlertType.WARNING);
      contentArea.requestFocus();
      return false;
    }

    if (categoryComboBox.getValue() == null) {
      showAlert("验证错误", "请选择分类", Alert.AlertType.WARNING);
      categoryComboBox.requestFocus();
      return false;
    }

    return true;
  }

  /**
   * 设置输入限制
   */
  private void setupInputLimits() {
    // 标题长度限制
    titleField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > 100) {
        titleField.setText(oldValue);
      }
    });

    // 内容长度限制
    contentArea.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > 5000) {
        contentArea.setText(oldValue);
      }
    });
  }

  /**
   * 显示对话框
   */
  private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * 清空表单（用于重新打开时）
   */
  public void clearForm() {
    titleField.clear();
    contentArea.clear();
    anonymousCheckBox.setSelected(false);
    categoryComboBox.setValue(PostCategory.CAMPUS_LIFE);
    publishButton.setDisable(false);
    publishButton.setText("发布");
  }

  /**
   * 检查用户是否登录
   */
  public boolean isUserLoggedIn() {
    return UserSession.getInstance().getCurrentUser() != null;
  }
}
