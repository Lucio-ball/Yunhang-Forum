package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.service.strategy.impl.HotSortStrategy; // 确保导入排序策略类
import com.yunhang.forum.service.strategy.impl.TimeSortStrategy;
import com.yunhang.forum.util.LogUtil;
import com.yunhang.forum.util.ResourcePaths;
import com.yunhang.forum.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
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
          // 时间排序：按发布时间倒序
          postService.setSortStrategy(new TimeSortStrategy());
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
                      getClass().getResource(ResourcePaths.FXML_AUTH_POST_ITEM));
                  loader.load();
                  controller = loader.getController();
                } catch (IOException e) {
                  LogUtil.error("加载 PostItem.fxml 失败", e);
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
    // 开启虚拟线程处理耗时的 Service 调用 (模拟文件 IO)
    TaskRunner.runAsync(() -> {
      try {
        // 调用 Service 获取数据（支持搜索态 + 已按策略排序）
        List<Post> posts = postService.getVisiblePosts();

        // 回到 JavaFX UI 线程更新 ListView
        TaskRunner.runOnUI(() -> {
          postListView.getItems().setAll(posts);
        });
      } catch (Exception e) {
        LogUtil.error("加载帖子列表失败", e);
      }
    });
  }

  /**
   * 刷新帖子列表 【微调】同样改为异步模式
   */
  @FXML
  private void refreshPosts() {
    TaskRunner.runAsync(() -> {
      List<Post> refreshedPosts = postService.refreshPosts();
      TaskRunner.runOnUI(() -> postListView.getItems().setAll(refreshedPosts));
    });
  }

  /**
   * 设置按分类筛选
   */
  public void filterByCategory(String category) {
    // 实际项目中应调用异步加载
    loadPosts(); // 暂时加载所有帖子
  }
}
