package com.yunhang.forum.model.entity;

import com.yunhang.forum.model.enums.EventType;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 帖子实体类 - 论坛内容的核心载体
 */
public class Post extends ObservableEntity {
  // 核心属性
  private String postId;
  private String title;
  private String content;
  private String authorId;
  private PostCategory category;
  private PostStatus status;
  private LocalDateTime publishTime;
  private LocalDateTime updateTime;
  private int viewCount;
  private int likeCount;
  private int commentCount;
  // 新增：点赞用户集合（用于判断某用户是否已点赞）
  private Set<String> likedUserIds = new HashSet<>();
  private List<PostImage> postImages;
  private boolean isAnonymous;
  private boolean isSensitive;
  private boolean isForceDeleted;
  // 新增：评论存储
  private final List<Comment> comments = new ArrayList<>();

  // 常量定义
  private static final double HOT_SCORE_VIEW_WEIGHT = 0.3;
  private static final double HOT_SCORE_LIKE_WEIGHT = 0.4;
  private static final double HOT_SCORE_COMMENT_WEIGHT = 0.3;
  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  // 构造方法
  public Post() {
    this.postId = UUID.randomUUID().toString();
    this.status = PostStatus.DRAFT;
    this.publishTime = LocalDateTime.now();
    this.updateTime = LocalDateTime.now();
    this.viewCount = 0;
    this.likeCount = 0;
    this.commentCount = 0;
    this.postImages = new ArrayList<>();
    this.isAnonymous = false;
    this.isSensitive = false;
    this.isForceDeleted = false;
  }

  public Post(String title, String content, String authorId, PostCategory category) {
    this();
    this.title = title;
    this.content = content;
    this.authorId = authorId;
    this.category = category;
  }

  /**
   * 仅用于初始化模拟数据：在实体内部完成计数与时间的设定，避免外部直接 setCount。
   */
  public static Post seeded(String title, String content, String authorId, PostCategory category,
      int views, int likes, int comments, LocalDateTime publishTime) {
    Post post = new Post(title, content, authorId, category);

    post.status = PostStatus.PUBLISHED;
    post.publishTime = (publishTime != null) ? publishTime : LocalDateTime.now();
    post.updateTime = post.publishTime;

    post.viewCount = Math.max(0, views);
    post.likeCount = Math.max(0, likes);
    post.commentCount = Math.max(0, comments);

    return post;
  }

  public Post(String s) {
    this();
    this.title = "示例帖子";
    this.content = s;
    this.authorId = "example_author";
    this.category = PostCategory.ACTIVITY;
  }

  // ==================== Getter和Setter方法 ====================

  public String getPostId() { return postId; }
  public void setPostId(String postId) { this.postId = postId; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getContent() { return content; }
  public void setContent(String content) {
    this.content = content;
    this.updateTime = LocalDateTime.now();
  }

  public String getAuthorId() { return authorId; }
  public void setAuthorId(String authorId) { this.authorId = authorId; }

  public PostCategory getCategory() { return category; }
  public void setCategory(PostCategory category) { this.category = category; }

  public PostStatus getStatus() { return status; }
  public void setStatus(PostStatus status) {
    this.status = status;
    this.updateTime = LocalDateTime.now();
  }

  public LocalDateTime getPublishTime() { return publishTime; }
  public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

  public LocalDateTime getUpdateTime() { return updateTime; }
  public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

  public int getViewCount() { return viewCount; }
  public void setViewCount(int viewCount) { this.viewCount = viewCount; }

  public int getLikeCount() { return likeCount; }

  public int getCommentCount() { return commentCount; }

  public List<PostImage> getPostImages() { return postImages; }
  public void setPostImages(List<PostImage> postImages) { this.postImages = postImages; }

  public boolean isAnonymous() { return isAnonymous; }
  public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

  public boolean isSensitive() { return isSensitive; }
  public void setSensitive(boolean sensitive) { isSensitive = sensitive; }

  public boolean isForceDeleted() { return isForceDeleted; }
  public void setForceDeleted(boolean forceDeleted) { isForceDeleted = forceDeleted; }

  // ==================== 业务方法 ====================

  /**
   * 发布帖子
   */
  public boolean publish() {
    if (this.status == PostStatus.DRAFT) {
      this.status = PostStatus.PUBLISHED;
      this.publishTime = LocalDateTime.now();
      this.updateTime = LocalDateTime.now();
      return true;
    }
    return false;
  }

  /**
   * 匿名发布帖子
   */
  public boolean publishAnonymously() {
    boolean success = publish();
    if (success) {
      this.isAnonymous = true;
    }
    return success;
  }

  /**
   * 软删除帖子（用户删除）
   */
  public void softDelete() {
    if (this.status != PostStatus.DELETED) {
      this.status = PostStatus.DELETED;
      this.updateTime = LocalDateTime.now();
    }
  }

  /**
   * 恢复已删除的帖子
   */
  public boolean restore() {
    if (this.status == PostStatus.DELETED && !this.isForceDeleted) {
      this.status = PostStatus.PUBLISHED;
      this.updateTime = LocalDateTime.now();
      return true;
    }
    return false;
  }

  /**
   * 管理员强制删除帖子
   */
  public boolean forceDelete() {
    if (this.status != PostStatus.DELETED) {
      this.status = PostStatus.DELETED;
      this.isForceDeleted = true;
      this.updateTime = LocalDateTime.now();
      return true;
    }
    return false;
  }

  /**
   * 计算热度得分
   * 公式：热度 = 浏览量 * 0.3 + 点赞数 * 0.4 + 评论数 * 0.3
   */
  public double calculateHotScore() {
    return viewCount * HOT_SCORE_VIEW_WEIGHT +
        likeCount * HOT_SCORE_LIKE_WEIGHT +
        commentCount * HOT_SCORE_COMMENT_WEIGHT;
  }

  /**
   * 获取热度得分（兼容方法）
   */
  public double getHotScore() {
    return calculateHotScore();
  }

  /**
   * 增加浏览量
   */
  public void incrementViewCount() {
    this.viewCount++;
    this.updateTime = LocalDateTime.now();
  }

  /**
   * 增加点赞数
   */
  public void incrementLikeCount() {
    this.likeCount++;
    this.updateTime = LocalDateTime.now();
  }

  /**
   * 判断指定用户是否已点赞。
   */
  public boolean isLikedBy(String userId) {
    if (userId == null || userId.isBlank()) {
      return false;
    }
    if (likedUserIds == null) {
      likedUserIds = new HashSet<>();
    }
    return likedUserIds.contains(userId);
  }

  /**
   * 切换点赞状态。
   * @return 切换后是否为“已点赞”状态
   */
  public boolean toggleLike(String userId) {
    if (userId == null || userId.isBlank()) {
      return false;
    }
    if (likedUserIds == null) {
      likedUserIds = new HashSet<>();
    }

    boolean liked;
    if (likedUserIds.add(userId)) {
      likeCount++;
      liked = true;
    } else {
      likedUserIds.remove(userId);
      likeCount = Math.max(0, likeCount - 1);
      liked = false;
    }
    updateTime = LocalDateTime.now();
    return liked;
  }

  /**
   * 增加评论数
   */
  public void incrementCommentCount() {
    this.commentCount++;
    this.updateTime = LocalDateTime.now();
  }

  // ==================== 内容管理方法 ====================

  /**
   * 添加图片资源到帖子
   */
  public void addImage(PostImage image) {
    if (image != null && image.isValid()) {
      this.postImages.add(image);
      this.updateTime = LocalDateTime.now();
    }
  }

  /**
   * 批量添加图片
   */
  public void addImages(List<PostImage> images) {
    if (images != null) {
      for (PostImage image : images) {
        addImage(image);
      }
    }
  }

  /**
   * 移除图片
   */
  public boolean removeImage(PostImage image) {
    boolean removed = this.postImages.remove(image);
    if (removed) {
      this.updateTime = LocalDateTime.now();
    }
    return removed;
  }

  /**
   * 清空所有图片
   */
  public void clearImages() {
    if (!postImages.isEmpty()) {
      postImages.clear();
      this.updateTime = LocalDateTime.now();
    }
  }

  /**
   * 生成内容摘要
   */
  public String getContentSummary(int maxLength) {
    if (content == null) {
      return "";
    }
    if (content.length() <= maxLength) {
      return content;
    }
    return content.substring(0, maxLength) + "...";
  }

  /**
   * 获取完整内容
   */
  public String getFullContent() {
    return content != null ? content : "";
  }

  /**
   * 将正文内容按行分割
   */
  public List<String> getContentLines() {
    List<String> lines = new ArrayList<>();
    if (content != null) {
      String[] splitLines = content.split("\n");
      for (String line : splitLines) {
        if (!line.trim().isEmpty()) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  // ==================== 展示相关方法 ====================

  /**
   * 获取显示的作者名称
   */
  public String getDisplayAuthor() {
    if (isAnonymous) {
      return "某同学";
    } else if (authorId != null && authorId.length() >= 8) {
      return "用户" + authorId.substring(0, 8);
    } else if (authorId != null) {
      return "用户" + authorId;
    } else {
      return "匿名用户";
    }
  }

  /**
   * 获取格式化的发布时间
   */
  public String getFormattedPublishTime() {
    return publishTime != null ? publishTime.format(TIME_FORMATTER) : "";
  }

  /**
   * 获取格式化的更新时间
   */
  public String getFormattedUpdateTime() {
    return updateTime != null ? updateTime.format(TIME_FORMATTER) : "";
  }

  /**
   * 获取分类的显示文本
   */
  public String getCategoryDisplay() {
    return category != null ? category.getDisplayText() : "未分类";
  }

  // ==================== 权限验证方法 ====================

  /**
   * 验证用户编辑权限
   */
  public boolean canBeEditedBy(String userId) {
    return userId != null && userId.equals(authorId) &&
        status != null && status.isEditable();
  }

  /**
   * 管理员查看真实作者信息
   */
  public String getRealAuthorForAdmin(String adminId) {
    // 简化：假设adminId以"admin_"开头的就是管理员
    if (adminId != null && adminId.startsWith("admin_")) {
      return authorId != null ? authorId : "未知作者";
    }
    return "无权查看";
  }

  /**
   * 判断是否允许评论
   */
  public boolean isCommentable() {
    return status != null && status.canComment();
  }

  /**
   * 判断帖子是否有图片
   */
  public boolean hasImages() {
    return !postImages.isEmpty();
  }

  /**
   * 获取图片数量
   */
  public int getImageCount() {
    return postImages.size();
  }

  /**
   * 根据文件名查找图片
   */
  public PostImage findImageByOriginalName(String originalName) {
    if (originalName == null || postImages.isEmpty()) {
      return null;
    }
    for (PostImage image : postImages) {
      if (originalName.equals(image.getOriginalName())) {
        return image;
      }
    }
    return null;
  }

  /**
   * 获取所有图片的Web路径（用于前端显示）
   */
  public List<String> getAllImageWebPaths() {
    List<String> paths = new ArrayList<>();
    for (PostImage image : postImages) {
      paths.add(image.getWebPath());
    }
    return paths;
  }

  /**
   * 获取所有缩略图的Web路径
   */
  public List<String> getAllThumbnailWebPaths() {
    List<String> paths = new ArrayList<>();
    for (PostImage image : postImages) {
      paths.add(image.getThumbnailWebPath());
    }
    return paths;
  }

  // ==================== 字段验证方法 ====================

  /**
   * 验证标题
   */
  public boolean validateTitle() {
    return title != null && !title.trim().isEmpty() && title.length() <= 100;
  }

  /**
   * 验证正文内容
   */
  public boolean validateContent() {
    return content != null && !content.trim().isEmpty() && content.length() <= 5000;
  }

  /**
   * 验证板块分类
   */
  public boolean validateCategory() {
    return category != null;
  }

  /**
   * 综合验证发布条件
   */
  public boolean isPublishable() {
    return validateTitle() && validateContent() && validateCategory() &&
        status == PostStatus.DRAFT;
  }

  // ==================== 排序比较器 ====================

  /**
   * 创建按热度降序排序的比较器
   */
  public static Comparator<Post> getHotnessComparator() {
    return (p1, p2) -> Double.compare(p2.calculateHotScore(), p1.calculateHotScore());
  }

  /**
   * 创建按发布时间降序排序的比较器
   */
  public static Comparator<Post> getTimeComparator() {
    return (p1, p2) -> {
      if (p1.publishTime == null && p2.publishTime == null) return 0;
      if (p1.publishTime == null) return 1;
      if (p2.publishTime == null) return -1;
      return p2.publishTime.compareTo(p1.publishTime);
    };
  }

  // ==================== 数据转换方法 ====================

  /**
   * 转换为列表页视图数据
   */
  public Map<String, Object> toListViewModel() {
    Map<String, Object> map = new HashMap<>();
    map.put("postId", postId);
    map.put("title", title);
    map.put("author", getDisplayAuthor());
    map.put("summary", getContentSummary(50));
    map.put("publishTime", getFormattedPublishTime());
    map.put("hotScore", calculateHotScore());
    map.put("category", getCategoryDisplay());
    map.put("viewCount", viewCount);
    map.put("likeCount", likeCount);
    map.put("commentCount", commentCount);
    map.put("status", status != null ? status.getStatusName() : "未知");
    map.put("imageCount", getImageCount());
    map.put("hasImages", hasImages());
    return map;
  }

  /**
   * 转换为详情页视图数据
   */
  public Map<String, Object> toDetailViewModel() {
    Map<String, Object> map = toListViewModel();
    map.put("fullContent", getFullContent());
    map.put("contentLines", getContentLines());
    map.put("images", postImages);
    map.put("isAnonymous", isAnonymous);
    map.put("isSensitive", isSensitive);
    map.put("updateTime", getFormattedUpdateTime());
    map.put("commentable", isCommentable());
    map.put("editable", status.isEditable());
    map.put("imageWebPaths", getAllImageWebPaths());
    map.put("thumbnailWebPaths", getAllThumbnailWebPaths());
    return map;
  }

  // ==================== 状态转换验证 ====================

  /**
   * 验证是否可以转换到目标状态
   */
  public boolean canTransitionTo(PostStatus targetStatus) {
    return this.status.canTransitionTo(targetStatus);
  }

  /**
   * 安全地转换状态
   */
  public boolean safeTransitionTo(PostStatus targetStatus) {
    if (canTransitionTo(targetStatus)) {
      this.status = targetStatus;
      this.updateTime = LocalDateTime.now();
      return true;
    }
    return false;
  }

  // ==================== 其他工具方法 ====================

  /**
   * 判断帖子是否可见（未被删除）
   */
  public boolean isVisible() {
    return status != null && status.isVisible() && !isForceDeleted;
  }

  /**
   * 判断帖子是否可编辑
   */
  public boolean isEditable() {
    return status != null && status.isEditable();
  }

  /**
   * 获取帖子创建后的天数
   */
  public long getDaysSinceCreation() {
    if (publishTime == null) {
      return 0;
    }
    return java.time.Duration.between(publishTime, LocalDateTime.now()).toDays();
  }

  /**
   * 判断是否是热门帖子（热度大于10）
   */
  public boolean isHot() {
    return calculateHotScore() > 10.0;
  }

  /**
   * 判断是否是今日发布的帖子
   */
  public boolean isToday() {
    if (publishTime == null) {
      return false;
    }
    LocalDateTime today = LocalDateTime.now();
    return publishTime.toLocalDate().equals(today.toLocalDate());
  }

  /**
   * 获取帖子的简短标识
   */
  public String getShortIdentifier() {
    if (postId == null || postId.length() < 8) {
      return postId;
    }
    return postId.substring(0, 8) + "...";
  }

  // 新增：评论逻辑与观察者通知
  /**
   * 向帖子添加一条评论，并自动通知观察者（尤其是作者）。
   * 关键逻辑：
   * 1) 存储评论；2) 递增评论计数；3) 构造事件；4) 通知观察者。
   */
  public void addComment(User commenter, String content) {
    if (!isCommentable()) {
      return;
    }
    Comment comment = new Comment(this.postId, commenter.getStudentID(), null, content);
    this.comments.add(comment);
    incrementCommentCount();

    // 确保作者在观察者列表中（兼容：authorId 可能是 studentId 或旧的 userID）
    User author = resolveAuthor();
    if (author != null) {
      this.addObserver(author);
    }

    // 创建并分发事件
    Event event = new Event(
        EventType.COMMENT_CREATED,
        commenter,
        String.format("%s 评论了你的帖子《%s》：%s", commenter.getNickname(), this.title, content)
    );
    notifyObservers(event);
  }

  // 与类图对齐：按 Comment 入参的重载
  public void addComment(Comment comment) {
    if (comment == null) {
      return;
    }
    User commenter = GlobalVariables.userMap.get(comment.getAuthorId());
    if (commenter == null) {
      this.comments.add(comment);
      incrementCommentCount();
      return;
    }
    addComment(commenter, comment.getContent());
  }

  /**
   * 解析作者对象。
   * 规范：authorId = studentID。
   * 兼容：若旧数据把 authorId 写成 userID(UUID)，这里做一次回退查找。
   */
  private User resolveAuthor() {
    if (this.authorId == null) {
      return null;
    }

    User byStudentId = GlobalVariables.userMap.get(this.authorId);
    if (byStudentId != null) {
      return byStudentId;
    }

    // Backward compatibility: authorId stored as userID (UUID)
    for (User u : GlobalVariables.userMap.values()) {
      if (u != null && this.authorId.equals(u.getUserID())) {
        return u;
      }
    }

    return null;
  }

  // 与类图的作者关联：提供便捷访问
  public User getAuthor() {
    return resolveAuthor();
  }

  // 兼容：提供一个与类图语义一致的无返回版本（不改变现有行为）
  public void publishVoid() {
    publish();
  }

  public List<Comment> getComments() { return new ArrayList<>(comments); }

  @Override
  public String toString() {
    return "Post{" +
        "postId='" + getShortIdentifier() + '\'' +
        ", title='" + title + '\'' +
        ", author='" + getDisplayAuthor() + '\'' +
        ", category=" + getCategoryDisplay() +
        ", status=" + (status != null ? status.getStatusName() : "未知") +
        ", publishTime=" + getFormattedPublishTime() +
        ", viewCount=" + viewCount +
        ", likeCount=" + likeCount +
        ", commentCount=" + commentCount +
        ", hotScore=" + String.format("%.2f", calculateHotScore()) +
        ", imageCount=" + getImageCount() +
        ", isAnonymous=" + isAnonymous +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Post post = (Post) o;
    return Objects.equals(postId, post.postId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postId);
  }
}
