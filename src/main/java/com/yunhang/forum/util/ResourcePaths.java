package com.yunhang.forum.util;

/**
 * 统一管理资源路径（拒绝硬编码）。
 */
public final class ResourcePaths {
  private ResourcePaths() {}

  public static final String FXML_PREFIX = "/com/yunhang/forum/fxml/";

  public static final String CSS_PREFIX = "/com/yunhang/forum/css/";
  public static final String CSS_STYLE = CSS_PREFIX + "style.css";

  // -------- Auth --------
  public static final String FXML_AUTH_LOGIN = FXML_PREFIX + "auth/Login.fxml";
  public static final String FXML_AUTH_REGISTER = FXML_PREFIX + "auth/Register.fxml";
  public static final String FXML_AUTH_USER_PROFILE = FXML_PREFIX + "auth/UserProfile.fxml";
  public static final String FXML_AUTH_POST_LIST = FXML_PREFIX + "auth/PostList.fxml";
  public static final String FXML_AUTH_POST_ITEM = FXML_PREFIX + "auth/PostItem.fxml";

  // -------- Main --------
  public static final String FXML_MAIN_LAYOUT = FXML_PREFIX + "auth/main/MainLayout.fxml";

  // -------- Post --------
  public static final String FXML_POST_EDITOR = FXML_PREFIX + "post/PostEditor.fxml";
  public static final String FXML_POST_DETAIL = FXML_PREFIX + "post/PostDetail.fxml";

  // -------- User --------
  public static final String FXML_USER_SETTINGS = FXML_PREFIX + "user/Settings.fxml";
}
