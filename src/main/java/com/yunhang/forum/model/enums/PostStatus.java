package com.yunhang.forum.model.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * 帖子状态枚举
 * 管理帖子在系统中的完整生命周期状态
 */
public enum PostStatus {
  DRAFT("草稿", false, true, false),
  PUBLISHED("已发布", true, true, true),
  DELETED("已删除", false, false, false),
  LOCKED("已锁定", true, false, false),
  ARCHIVED("已归档", true, false, false);

  private final String statusName;
  private final boolean isVisible;
  private final boolean isEditable;
  private final boolean canComment;

  PostStatus(String statusName, boolean isVisible, boolean isEditable, boolean canComment) {
    this.statusName = statusName;
    this.isVisible = isVisible;
    this.isEditable = isEditable;
    this.canComment = canComment;
  }

  // Getter方法
  public String getStatusName() {
    return statusName;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public boolean isEditable() {
    return isEditable;
  }

  public boolean canComment() {
    return canComment;
  }

  /**
   * 获取可转换的下一个状态集合
   */
  public Set<PostStatus> getNextValidStates() {
    Set<PostStatus> nextStates = EnumSet.noneOf(PostStatus.class);

    switch (this) {
      case DRAFT:
        nextStates.add(PUBLISHED);
        nextStates.add(DELETED);
        break;
      case PUBLISHED:
        nextStates.add(DELETED);
        nextStates.add(LOCKED);
        nextStates.add(ARCHIVED);
        break;
      case DELETED:
        nextStates.add(PUBLISHED);
        break;
      case LOCKED:
        nextStates.add(PUBLISHED);
        nextStates.add(DELETED);
        break;
      case ARCHIVED:
        // 归档状态通常是终止状态
        break;
    }
    return nextStates;
  }

  /**
   * 验证状态转换是否允许
   */
  public boolean canTransitionTo(PostStatus targetStatus) {
    return getNextValidStates().contains(targetStatus);
  }

  /**
   * 判断是否为终止状态
   */
  public boolean isTerminalState() {
    return this == ARCHIVED || this == DELETED;
  }
}
