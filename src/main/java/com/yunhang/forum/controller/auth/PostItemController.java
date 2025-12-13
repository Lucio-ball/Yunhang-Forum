package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 帖子卡片控制器 - 控制单个帖子卡片的显示
 */
public class PostItemController {

  @FXML
  private HBox rootContainer;

  @FXML
  private ImageView avatarImageView;

  @FXML
  private Label titleLabel;

  @FXML
  private Label summaryLabel;

  @FXML
  private Label categoryLabel;

  @FXML
  private Label timeLabel;

  @FXML
  private Label likesLabel;

  @FXML
  private Label commentsLabel;

  @FXML
  private Label viewsLabel;

  @FXML
  private VBox rightContainer;

  @FXML
  private HBox tagContainer;

  // 默认头像
  private static final String DEFAULT_AVATAR = "/com/yunhang/forum/images/default-avatar.png";

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    // 设置头像为圆形
    avatarImageView.setClip(new javafx.scene.shape.Circle(25, 25, 25));

    // 加载默认头像
    try {
      Image defaultAvatar = new Image(getClass().getResourceAsStream(DEFAULT_AVATAR));
      avatarImageView.setImage(defaultAvatar);
    } catch (Exception e) {
      // 如果找不到默认头像，使用占位符
      System.err.println("无法加载默认头像: " + DEFAULT_AVATAR);
    }
  }

  /**
   * 设置帖子数据到UI组件
   */
  public void setPostData(Post post) {
    if (post == null) {
      return;
    }

    // 设置标题
    titleLabel.setText(post.getTitle());

    // 设置内容摘要（截取前50字符）
    String content = post.getContent();
    if (content != null && content.length() > 50) {
      summaryLabel.setText(content.substring(0, 50) + "...");
    } else {
      summaryLabel.setText(content != null ? content : "");
    }

    // 设置分类标签
    setCategoryTag(post.getCategory());

    // 设置时间
    timeLabel.setText(DateUtil.getRelativeTime(post.getPublishTime()));

    // 设置点赞数
    likesLabel.setText("♥ " + post.getLikeCount());

    // 设置评论数
    commentsLabel.setText("💬 " + post.getCommentCount());

    // 设置浏览数
    viewsLabel.setText("👁 " + post.getViewCount());

    // 设置作者头像（这里简化处理，实际应该根据作者ID加载）
    setAuthorAvatar(post.getAuthorId(), post.isAnonymous());
  }

  /**
   * 设置分类标签样式（根据分类动态设置样式）
   */
  private void setCategoryTag(PostCategory category) {
    if (category == null) {
      categoryLabel.setText("未分类");
      categoryLabel.setStyle(getTagStyle("#9e9e9e", "#ffffff"));
      return;
    }

    categoryLabel.setText(category.getCategoryName());

    // 根据不同的分类设置不同的样式
    switch (category) {
      case LEARNING:
        categoryLabel.setStyle(getTagStyle("#e3f2fd", "#1976d2"));
        break;
      case CAMPUS_LIFE:
        categoryLabel.setStyle(getTagStyle("#f3e5f5", "#7b1fa2"));
        break;
      case SECOND_HAND:
        categoryLabel.setStyle(getTagStyle("#fff3e0", "#ef6c00"));
        break;
      case ACTIVITY:
        categoryLabel.setStyle(getTagStyle("#e8f5e9", "#2e7d32"));
        break;
      case QNA:
        categoryLabel.setStyle(getTagStyle("#fff8e1", "#ff8f00"));
        break;
      case EMPLOYMENT:
        categoryLabel.setStyle(getTagStyle("#f1f8e9", "#558b2f"));
        break;
      case ANNOUNCEMENT:
        categoryLabel.setStyle(getTagStyle("#ffebee", "#c62828"));
        break;
      default:
        categoryLabel.setStyle(getTagStyle("#f5f5f5", "#616161"));
        break;
    }
  }

  /**
   * 获取标签样式
   */
  private String getTagStyle(String backgroundColor, String textColor) {
    return String.format(
        "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-background-radius: 4; " +
            "-fx-padding: 2 8 2 8; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;",
        backgroundColor, textColor
    );
  }

  /**
   * 设置作者头像
   */
  private void setAuthorAvatar(String authorId, boolean isAnonymous) {
    if (isAnonymous) {
      // 匿名帖子使用默认头像
      return;
    }

    // 在实际项目中，这里应该根据authorId加载对应的头像
    // 这里简化处理，使用默认头像
    try {
      String avatarPath = String.format("/com/yunhang/forum/images/avatars/%s.png",
          authorId.substring(0, Math.min(8, authorId.length())));

      Image avatar = new Image(getClass().getResourceAsStream(avatarPath));
      if (avatar != null && !avatar.isError()) {
        avatarImageView.setImage(avatar);
      }
    } catch (Exception e) {
      // 如果找不到用户头像，继续使用默认头像
    }
  }

  /**
   * 获取根容器（用于ListView的Cell）
   */
  public HBox getRootContainer() {
    return rootContainer;
  }
}
