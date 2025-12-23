package com.yunhang.forum.controller.post;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.LogUtil;
import com.yunhang.forum.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
    // 1. 设置分类下拉框的转换器，将枚举值映射为中文显示
    categoryComboBox.setConverter(new StringConverter<PostCategory>() {
      @Override
      public String toString(PostCategory category) {
        if (category == null)
          return "";
        // 根据枚举值返回对应的中文
        switch (category) {
          case LEARNING:
            return "学习交流";
          case CAMPUS_LIFE:
            return "校园生活";
          case SECOND_HAND:
            return "二手交易";
          case ACTIVITY:
            return "活动召集";
          case QNA:
            return "问答求助";
          case EMPLOYMENT:
            return "就业实习";
          case ANNOUNCEMENT:
            return "官方公告";
          default:
            return category.toString();
        }
      }

      @Override
      public PostCategory fromString(String string) {
        return null; // 下拉框不需要从字符串还原对象
      }
    });

    // 2. 初始化分类下拉框数据
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

    // 使用统一的用户标识（与点赞/评论逻辑保持一致）
    String authorId = currentUser.getStudentID();

    // 构建帖子对象
    Post post = new Post(titleField.getText().trim(), contentArea.getText().trim(), authorId,
        categoryComboBox.getValue());

    // 设置匿名状态
    post.setAnonymous(anonymousCheckBox.isSelected());

    // 禁用按钮，显示加载状态
    publishButton.setDisable(true);
    publishButton.setText("发布中...");

    // 异步创建帖子（虚拟线程，避免阻塞 UI）
    TaskRunner.runAsync(() -> {
      try {
        // 调用 Service 创建帖子（写入缓存/持久化入口）
        PostService.getInstance().createPost(post);

        // 切换到UI线程更新界面
        TaskRunner.runOnUI(() -> {
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
        LogUtil.error("Post publish failed", e);
        TaskRunner.runOnUI(() -> {
          showAlert("错误", "发布失败: " + e.getMessage(), Alert.AlertType.ERROR);
          publishButton.setDisable(false);
          publishButton.setText("发布");
        });
      }
    });
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
