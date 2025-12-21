package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.service.strategy.HotSortStrategy; // 确保导入排序策略类
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * 帖子列表控制器 - 控制帖子列表页面的显示
 */
public class PostListController {

  @FXML
  private ListView<Post> postListView;

  @FXML
  private Button refreshButton;

  @FXML
  private ComboBox<String> sortComboBox; // 【新增】对应 FXML 中的 ID

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

    // 【新增】初始化排序下拉框逻辑
    initSortComboBox();

    // 加载帖子数据 (改为异步加载)
    loadPosts();
  }

  /**
   * 【新增】初始化排序下拉框及其监听器
   */
  private void initSortComboBox() {
    if (sortComboBox != null) {
      sortComboBox.getItems().addAll("最新发布", "最多热门");
      sortComboBox.setValue("最新发布");

      sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if ("最多热门".equals(newVal)) {
          postService.setSortStrategy(new HotSortStrategy());
        } else {
          // 时间排序：按发布时间倒序 (Lambda 实现)
          postService.setSortStrategy(
              posts -> posts.sort(Comparator.comparing(Post::getPublishTime).reversed()));
        }
        loadPosts(); // 切换策略后重新异步加载
      });
    }
  }

  /**
   * 配置ListView
   */
  private void configureListView() {
    // 增量添加样式类，避免覆盖控件默认 styleClass
    postListView.getStyleClass().add("post-list-view");

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
                      getClass().getResource("/com/yunhang.forum/fxml/auth/PostItem.fxml"));
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
  }

  /**
   * 加载帖子数据 【微调】使用异步线程处理，防止界面卡顿
   */
  private void loadPosts() {
    // 开启新线程处理耗时的 Service 调用 (模拟文件 IO)
    new Thread(() -> {
      try {
        // 调用 Service 获取数据 (Service 内部已根据策略排好序)
        List<Post> posts = postService.getAllPosts();

        // 回到 JavaFX UI 线程更新 ListView
        Platform.runLater(() -> {
          postListView.getItems().setAll(posts);
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  /**
   * 刷新帖子列表 【微调】同样改为异步模式
   */
  @FXML
  private void refreshPosts() {
    new Thread(() -> {
      List<Post> refreshedPosts = postService.refreshPosts();
      Platform.runLater(() -> {
        postListView.getItems().setAll(refreshedPosts);
      });
    }).start();
  }

  /**
   * 设置按分类筛选
   */
  public void filterByCategory(String category) {
    // 实际项目中应调用异步加载
    loadPosts(); // 暂时加载所有帖子
  }
}
