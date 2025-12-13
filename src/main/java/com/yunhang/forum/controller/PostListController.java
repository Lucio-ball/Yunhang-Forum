package com.yunhang.forum.controller;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 帖子列表控制器 - 主列表页面
 */
public class PostListController {

  @FXML
  private ListView<Post> postListView;

  @FXML
  private Button refreshButton;

  @FXML
  private Label statusLabel;

  private PostService postService;

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    postService = new PostService();

    // 设置ListView的单元格工厂
    setupCellFactory();

    // 加载初始数据
    loadPosts();

    // 设置刷新按钮事件
    refreshButton.setOnAction(event -> refreshPosts());

    // 设置ListView选择监听
    postListView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            onPostSelected(newValue);
          }
        }
    );
  }

  /**
   * 设置单元格工厂
   */
  private void setupCellFactory() {
    postListView.setCellFactory(new Callback<ListView<Post>, ListCell<Post>>() {
      @Override
      public ListCell<Post> call(ListView<Post> listView) {
        return new ListCell<Post>() {
          private HBox cellRoot;
          private PostItemController controller;

          {
            try {
              // 加载FXML文件
              URL fxmlLocation = getClass().getResource(
                  "/com/yunhang/forum/fxml/post/PostItem.fxml"
              );

              if (fxmlLocation != null) {
                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                cellRoot = loader.load();
                controller = loader.getController();
              } else {
                // 如果FXML文件不存在，创建一个简单的占位符
                cellRoot = new HBox();
                cellRoot.setPrefHeight(100);
              }
            } catch (IOException e) {
              e.printStackTrace();
              cellRoot = new HBox();
              cellRoot.setPrefHeight(100);
            }
          }

          @Override
          protected void updateItem(Post post, boolean empty) {
            super.updateItem(post, empty);

            if (empty || post == null) {
              setText(null);
              setGraphic(null);
            } else {
              // 使用缓存的controller设置数据
              if (controller != null) {
                controller.setPostData(post);
              }

              // 设置单元格的图形
              setText(null);
              setGraphic(cellRoot);

              // 设置单元格样式
              setStyle(
                  "-fx-background-color: transparent;" +
                      "-fx-padding: 5 0 5 0;"
              );
            }
          }
        };
      }
    });
  }

  /**
   * 加载帖子数据
   */
  private void loadPosts() {
    try {
      List<Post> posts = postService.getAllPosts();
      postListView.getItems().clear();
      postListView.getItems().addAll(posts);

      // 更新状态标签
      statusLabel.setText("已加载 " + posts.size() + " 条帖子");
      statusLabel.setStyle("-fx-text-fill: #4CAF50;");

    } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("加载帖子失败: " + e.getMessage());
      statusLabel.setStyle("-fx-text-fill: #f44336;");
    }
  }

  /**
   * 刷新帖子列表
   */
  private void refreshPosts() {
    try {
      List<Post> posts = postService.refreshPosts();
      postListView.getItems().clear();
      postListView.getItems().addAll(posts);

      // 显示刷新成功的消息
      statusLabel.setText("刷新成功！加载了 " + posts.size() + " 条帖子");
      statusLabel.setStyle("-fx-text-fill: #4CAF50;");

      // 短暂高亮刷新按钮
      refreshButton.setStyle(
          "-fx-background-color: #4CAF50; " +
              "-fx-text-fill: white;"
      );

      // 2秒后恢复按钮样式
      new Thread(() -> {
        try {
          Thread.sleep(2000);
          javafx.application.Platform.runLater(() -> {
            refreshButton.setStyle("");
          });
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }).start();

    } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("刷新失败: " + e.getMessage());
      statusLabel.setStyle("-fx-text-fill: #f44336;");
    }
  }

  /**
   * 帖子被选中时的处理
   */
  private void onPostSelected(Post post) {
    // 这里可以处理帖子点击事件，比如跳转到详情页
    System.out.println("帖子被选中: " + post.getTitle());

    // 在实际项目中，这里应该跳转到帖子详情页
    // ViewManager.switchContent("/com/yunhang/forum/fxml/post/PostDetail.fxml", post);
  }

  /**
   * 获取ListView实例（供外部调用）
   */
  public ListView<Post> getPostListView() {
    return postListView;
  }

  /**
   * 设置排序方式
   */
  public void setSortByHot() {
    List<Post> posts = postListView.getItems();
    posts.sort((p1, p2) -> Double.compare(p2.calculateHotScore(), p1.calculateHotScore()));
    postListView.refresh();
  }

  public void setSortByTime() {
    List<Post> posts = postListView.getItems();
    posts.sort((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime()));
    postListView.refresh();
  }
}
