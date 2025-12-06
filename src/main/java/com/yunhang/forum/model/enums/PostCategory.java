package com.yunhang.forum.model.enums;

/**
 * 帖子分类枚举
 * 规范化论坛内容板块，提供统一的分类标准
 */
public enum PostCategory {
  LEARNING("学习交流", "📚", 1),
  CAMPUS_LIFE("校园生活", "🏫", 2),
  SECOND_HAND("二手交易", "🛒", 3),
  ACTIVITY("活动召集", "🎉", 4),
  QNA("问答求助", "❓", 5),
  EMPLOYMENT("就业实习", "💼", 6),
  ANNOUNCEMENT("官方公告", "📢", 7);

  private final String categoryName;
  private final String icon;
  private final int sortWeight;

  PostCategory(String categoryName, String icon, int sortWeight) {
    this.categoryName = categoryName;
    this.icon = icon;
    this.sortWeight = sortWeight;
  }

  // Getter方法
  public String getCategoryName() {
    return categoryName;
  }

  public String getIcon() {
    return icon;
  }

  public int getSortWeight() {
    return sortWeight;
  }

  /**
   * 验证分类是否允许普通用户发布
   * 注：官方公告通常只允许管理员发布
   */
  public boolean isUserPostable() {
    return this != ANNOUNCEMENT;
  }

  /**
   * 获取带图标的显示文本
   */
  public String getDisplayText() {
    return icon + " " + categoryName;
  }
}
