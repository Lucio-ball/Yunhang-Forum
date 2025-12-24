package com.yunhang.forum.util;

import com.yunhang.forum.controller.post.PostDetailController;
import com.yunhang.forum.model.entity.Post;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * ViewManager: static utility for switching views in the main window.
 * 负责管理主舞台（Stage）上的场景（Scene）切换和主布局（BorderPane）的内容加载。
 */
public final class ViewManager {
    // 主布局容器，用于放置主界面中的 Header/Center/Footer
    private static BorderPane mainLayout;
    // 主窗口舞台
    private static Stage primaryStage;

    // 默认的 FXML 资源路径前缀
    private static final String FXML_PATH_PREFIX = ResourcePaths.FXML_PREFIX;
    // 默认的 CSS 资源路径前缀
    private static final String CSS_STYLE = ResourcePaths.CSS_STYLE; // 假定所有场景都使用这个CSS

    private ViewManager() {}

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    public static BorderPane getMainLayout() {
        return mainLayout;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // 辅助方法：统一资源查找和错误处理
    private static Parent loadFxmlResource(String fxmlPath) throws IOException {
        Objects.requireNonNull(fxmlPath, "fxmlPath");

        FXMLLoader loader = createLoader(fxmlPath);
        Parent root = loader.load();

        // Store controller for callers that need to trigger refresh hooks.
        Object controller = loader.getController();
        if (controller != null) {
            root.setUserData(controller);
        }

        return root;
    }

    private static FXMLLoader createLoader(String fxmlPath) throws IOException {
        Objects.requireNonNull(fxmlPath, "fxmlPath");
        String fullPath = normalizeFxmlPath(fxmlPath);
        URL resourceUrl = ViewManager.class.getResource(fullPath);
        if (resourceUrl == null) {
            LogUtil.warn("资源未找到! 尝试路径: " + fullPath);
            throw new IOException("FXML resource not found: " + fullPath);
        }
        return new FXMLLoader(resourceUrl);
    }

    /**
     * 将传入的 fxmlPath 规范化为可被 ClassLoader 解析的绝对路径。
     *
     * <p>支持两种写法：</p>
     * <ul>
     *   <li>相对路径：auth/Login.fxml（会自动拼接 FXML_PATH_PREFIX）</li>
     *   <li>绝对路径：/com/yunhang/forum/fxml/auth/Login.fxml（不会重复拼接）</li>
     * </ul>
     */
    private static String normalizeFxmlPath(String fxmlPath) {
        String trimmed = fxmlPath.trim();
        if (trimmed.startsWith("/")) {
            return trimmed;
        }
        return FXML_PATH_PREFIX + trimmed;
    }

    // 辅助方法：统一 CSS 查找
    private static String getCssUrl() {
        URL cssResource = ViewManager.class.getResource(CSS_STYLE);
        if (cssResource == null) {
            LogUtil.warn("警告：CSS 文件未找到! 尝试路径: " + CSS_STYLE);
            return null; // 返回 null，允许程序继续运行但没有样式
        }
        return cssResource.toExternalForm();
    }


    /**
     * 【重要】在主窗口内部加载内容到 BorderPane 的 Center 区域。
     * 绝对不要在这里切 Scene，否则会导致 MainLayout(sidebar/header) 消失。
     */
    public static void loadContent(String fxmlPath) {
        Objects.requireNonNull(mainLayout, "mainLayout is not initialized. Call setMainLayout() first.");

        try {
            // 使用辅助方法
            Parent content = loadFxmlResource(fxmlPath);
            mainLayout.setCenter(content);
        } catch (IOException e) {
            LogUtil.error("Failed to load FXML content: " + fxmlPath, e);
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    /**
     * 详情页路由：加载 PostDetail.fxml 并把 Post 注入 controller。
     * 用于列表点击进入详情。
     */
    public static void showPostDetail(Post post) {
        Objects.requireNonNull(mainLayout, "mainLayout is not initialized. Call setMainLayout() first.");
        if (post == null) {
            return;
        }

        try {
            FXMLLoader loader = createLoader(ResourcePaths.FXML_POST_DETAIL);
            Parent root = loader.load();
            PostDetailController controller = loader.getController();
            if (controller != null) {
                controller.initData(post);
            }
            mainLayout.setCenter(root);
        } catch (IOException e) {
            LogUtil.error("Failed to load post detail: " + e.getMessage(), e);
            throw new RuntimeException("Failed to show post detail", e);
        }
    }

    /**
     * 【新增】加载头部内容到 BorderPane 的 Top 区域
     * 适用于：登录成功后加载固定导航栏
     * @param fxmlPath 头部 FXML 路径 (如 "main/Header.fxml")
     */
    public static void loadHeader(String fxmlPath) {
        Objects.requireNonNull(mainLayout, "mainLayout is not initialized.");
        try {
            Parent header = loadFxmlResource(fxmlPath);
            mainLayout.setTop(header);
        } catch (IOException e) {
            LogUtil.error("Failed to load FXML header: " + fxmlPath, e);
            throw new RuntimeException("Failed to load header FXML.", e);
        }
    }

    /**
     * 【新增】加载底部内容到 BorderPane 的 Bottom 区域
     * 适用于：加载固定版权信息栏
     * @param fxmlPath 底部 FXML 路径 (如 "main/Footer.fxml")
     */
    public static void loadFooter(String fxmlPath) {
        Objects.requireNonNull(mainLayout, "mainLayout is not initialized.");
        try {
            Parent footer = loadFxmlResource(fxmlPath);
            mainLayout.setBottom(footer);
        } catch (IOException e) {
            LogUtil.error("Failed to load FXML footer: " + fxmlPath, e);
            throw new RuntimeException("Failed to load footer FXML.", e);
        }
    }

    /**
     * 【新增】切换整个 Stage 的 Scene
     * 适用于：未登录状态下的场景切换 (如 Login -> Register -> Login)
     * @param fxmlPath 相对于 FXML_PATH_PREFIX 的路径 (如 "auth/Register.fxml")
     */
    public static void switchScene(String fxmlPath) {
        Objects.requireNonNull(primaryStage, "primaryStage is not initialized. Call setPrimaryStage() first.");

        try {
            FXMLLoader loader = createLoader(fxmlPath);
            Parent root = loader.load();

            // If entering main shell, capture the BorderPane so loadContent works.
            if (ResourcePaths.FXML_MAIN_LAYOUT.equals(fxmlPath)) {
                if (root instanceof BorderPane bp) {
                    setMainLayout(bp);
                }
            }

            Scene scene = new Scene(root);

            String cssUrl = getCssUrl();
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl);
            }

            primaryStage.setScene(scene);
            if (primaryStage.getWidth() <= 0 || primaryStage.getHeight() <= 0) {
                primaryStage.sizeToScene();
            }
            primaryStage.centerOnScreen();

            // Default landing: once main layout is ready, show the Home welcome page.
            if (ResourcePaths.FXML_MAIN_LAYOUT.equals(fxmlPath)) {
                TaskRunner.runOnUI(() -> loadContent("main/Home.fxml"));
            }

        } catch (IOException e) {
            LogUtil.error("Failed to load FXML scene: " + fxmlPath, e);
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }

    @Deprecated
    public static void showLoginWindow() {
        switchScene(ResourcePaths.FXML_AUTH_LOGIN);
    }

    /**
     * If the current center view is MyPosts, trigger its refresh hook.
     * This helps keep notifications in sync after like/comment actions.
     */
    public static void refreshMyPostsIfVisible() {
        if (mainLayout == null) {
            return;
        }
        javafx.scene.Node center = mainLayout.getCenter();
        if (center != null && center.getUserData() instanceof com.yunhang.forum.controller.main.MyPostsController c) {
            c.refreshData();
        }
    }
}
