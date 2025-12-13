package com.yunhang.forum.controller;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.util.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 帖子卡片控制器 - 用于ListView中的单个帖子项
 */
public class PostItemController {

  // FXML组件
  @FXML
  private HBox rootHBox;

  @FXML
  private ImageView avatarImageView;

  @FXML
  private VBox contentVBox;

  @FXML
  private Label titleLabel;

  @FXML
  private Label summaryLabel;

  @FXML
  private HBox tagsHBox;

  @FXML
  private Label categoryTagLabel;

  @FXML
  private VBox statsVBox;

  @FXML
  private Label timeLabel;

  @FXML
  private Label likesLabel;

  @FXML
  private Label commentsLabel;

  @FXML
  private Label viewsLabel;

  // 当前显示的帖子
  private Post currentPost;

  // 分类样式映射
  private static final Map<PostCategory, String> CATEGORY_STYLES = new HashMap<>();

  static {
    // 学习交流 - 蓝色系
    CATEGORY_STYLES.put(PostCategory.LEARNING,
        "-fx-background-color: #e3f2fd; " +
            "-fx-text-fill: #1976d2; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 校园生活 - 绿色系
    CATEGORY_STYLES.put(PostCategory.CAMPUS_LIFE,
        "-fx-background-color: #e8f5e9; " +
            "-fx-text-fill: #388e3c; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 二手交易 - 橙色系
    CATEGORY_STYLES.put(PostCategory.SECOND_HAND,
        "-fx-background-color: #fff3e0; " +
            "-fx-text-fill: #f57c00; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 活动召集 - 红色系
    CATEGORY_STYLES.put(PostCategory.ACTIVITY,
        "-fx-background-color: #ffebee; " +
            "-fx-text-fill: #d32f2f; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 问答求助 - 紫色系
    CATEGORY_STYLES.put(PostCategory.QNA,
        "-fx-background-color: #f3e5f5; " +
            "-fx-text-fill: #7b1fa2; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 就业实习 - 青色系
    CATEGORY_STYLES.put(PostCategory.EMPLOYMENT,
        "-fx-background-color: #e0f2f1; " +
            "-fx-text-fill: #00796b; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");

    // 官方公告 - 金色系
    CATEGORY_STYLES.put(PostCategory.ANNOUNCEMENT,
        "-fx-background-color: #fffde7; " +
            "-fx-text-fill: #ff8f00; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 3 10 3 10; " +
            "-fx-font-size: 11px;");
  }

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    // 设置头像为圆形
    if (avatarImageView != null) {
      Circle clip = new Circle(25, 25, 25);
      avatarImageView.setClip(clip);
    }
  }

  /**
   * 设置帖子数据
   * @param post 帖子对象
   */
  public void setPostData(Post post) {
    this.currentPost = post;

    if (post == null) {
      return;
    }

    // 设置标题
    titleLabel.setText(post.getTitle());

    // 设置内容摘要
    String summary = post.getContentSummary(50);
    summaryLabel.setText(summary);

    // 设置分类标签
    updateTagStyle(post.getCategory());

    // 设置时间
    String friendlyTime = DateUtil.getFriendlyTime(post.getPublishTime());
    timeLabel.setText(friendlyTime);

    // 设置统计信息
    setStatistics(post);

    // 设置头像（根据是否为匿名）
    setAvatar(post);
  }

  /**
   * 更新分类标签样式
   * @param category 帖子分类
   */
  private void updateTagStyle(PostCategory category) {
    if (category == null || categoryTagLabel == null) {
      return;
    }

    // 设置标签文本
    categoryTagLabel.setText(category.getCategoryName());

    // 设置动态样式
    String style = CATEGORY_STYLES.get(category);
    if (style != null) {
      categoryTagLabel.setStyle(style);
    } else {
      // 默认样式
      categoryTagLabel.setStyle(
          "-fx-background-color: #f5f5f5; " +
              "-fx-text-fill: #666; " +
              "-fx-background-radius: 12; " +
              "-fx-padding: 3 10 3 10; " +
              "-fx-font-size: 11px;"
      );
    }
  }

  /**
   * 设置统计信息
   */
  private void setStatistics(Post post) {
    // 点赞数
    likesLabel.setText("❤ " + post.getLikeCount());

    // 评论数
    commentsLabel.setText("💬 " + post.getCommentCount());

    // 浏览量
    viewsLabel.setText("👁 " + post.getViewCount());

    // 热门帖子特殊样式
    if (post.isHot()) {
      rootHBox.setStyle(
          "-fx-border-color: #ff9800; " +
              "-fx-border-width: 2; " +
              "-fx-border-radius: 8; " +
              "-fx-background-radius: 8; " +
              "-fx-background-color: #fff8e1;"
      );
    } else {
      rootHBox.setStyle(
          "-fx-border-color: #e0e0e0; " +
              "-fx-border-width: 1; " +
              "-fx-border-radius: 8; " +
              "-fx-background-radius: 8; " +
              "-fx-background-color: white;"
      );
    }
  }

  /**
   * 设置头像
   */
  private void setAvatar(Post post) {
    try {
      String avatarPath;

      if (post.isAnonymous()) {
        // 匿名用户使用默认头像
        avatarPath = "/com/yunhang/forum/images/default_avatar.png";
      } else {
        // 根据用户ID生成不同头像（模拟）
        int hash = Math.abs(post.getAuthorId().hashCode() % 5);
        avatarPath = String.format("/com/yunhang/forum/images/avatar_%d.png", hash);
      }

      Image avatarImage = new Image(avatarPath);
      avatarImageView.setImage(avatarImage);

    } catch (Exception e) {
      // 如果头像加载失败，使用默认头像
      try {
        Image defaultImage = new Image("/com/yunhang/forum/images/default_avatar.png");
        avatarImageView.setImage(defaultImage);
      } catch (Exception ex) {
        // 如果连默认头像都没有，清空图片
        avatarImageView.setImage(null);
      }
    }
  }

  /**
   * 获取当前帖子
   */
  public Post getCurrentPost() {
    return currentPost;
  }

  /**
   * 获取根节点
   */
  public HBox getRoot() {
    return rootHBox;
  }
}
