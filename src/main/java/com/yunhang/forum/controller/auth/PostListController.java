package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;

/**
 * 帖子列表控制器 - 控制帖子列表页面的显示
 */
public class PostListController {

  @FXML
  private ListView<Post> postListView;

  @FXML
  private Button refreshButton;

  private PostService postService;

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    // 获取PostService实例
    postService = PostService.getInstance();

    // 配置ListView
    configureListView();

    // 加载帖子数据
    loadPosts();

    // 设置刷新按钮事件
    refreshButton.setOnAction(event -> refreshPosts());
  }

  /**
   * 配置ListView
   */
  private void configureListView() {
    // 设置Cell Factory
    postListView.setCellFactory(new Callback<>() {
      @Override
      public ListCell<Post> call(ListView<Post> param) {
        return new ListCell<>() {
          private FXMLLoader loader;
          private PostItemController controller;

          @Override
          protected void updateItem(Post post, boolean empty) {
            super.updateItem(post, empty);

            if (empty || post == null) {
              setText(null);
              setGraphic(null);
            } else {
              if (loader == null) {
                try {
                  // 加载FXML文件
                  loader = new FXMLLoader(
                      getClass().getResource(
                          "/com/yunhang/forum/fxml/post/PostItem.fxml"
                      )
                  );
                  loader.load();
                  controller = loader.getController();
                } catch (IOException e) {
                  e.printStackTrace();
                  setText("加载失败");
                  setGraphic(null);
                  return;
                }
              }

              // 设置帖子数据
              controller.setPostData(post);

              // 设置Cell的图形
              setGraphic(controller.getRootContainer());
              setText(null);
            }
          }
        };
      }
    });

    // 设置ListView的样式
    postListView.setStyle("-fx-background-color: transparent;");
    postListView.setPrefHeight(600);
  }

  /**
   * 加载帖子数据
   */
  private void loadPosts() {
    postListView.getItems().clear();
    postListView.getItems().addAll(postService.getAllPosts());
  }

  /**
   * 刷新帖子列表
   */
  @FXML
  private void refreshPosts() {
    postListView.getItems().clear();
    postListView.getItems().addAll(postService.refreshPosts());
  }

  /**
   * 设置按分类筛选
   */
  public void filterByCategory(String category) {
    // 这里可以根据category参数筛选帖子
    // 实际项目中应该调用PostService的相应方法
    loadPosts(); // 暂时加载所有帖子
  }
}
