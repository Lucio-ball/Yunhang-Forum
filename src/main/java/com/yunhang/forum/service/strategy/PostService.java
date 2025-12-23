package com.yunhang.forum.service.strategy;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;
import com.yunhang.forum.service.strategy.impl.TimeSortStrategy;
import com.yunhang.forum.service.strategy.impl.TitleKeywordStrategy;
import com.yunhang.forum.util.AppContext;
import com.yunhang.forum.util.LogUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子服务类 - 处理帖子相关的业务逻辑
 */
public class PostService {

  private static PostService instance;

  private volatile DataLoader dataLoader;

  /**
   * 简化：在内存中维护帖子列表，保证点赞/评论能按 postId 找到并更新。
   * （后续如接入 DataLoader，可在这里替换为持久化实现）
   */
  private final List<Post> cachedPosts = new ArrayList<>();
  private boolean initialized = false;

  // 维护当前的排序策略，默认为按时间排序
  private PostSortStrategy sortStrategy = new TimeSortStrategy();

  // 维护当前的搜索策略，默认为按标题关键字搜索
  private PostSearchStrategy searchStrategy = new TitleKeywordStrategy();

  // 维护当前的搜索关键字（由 UI 设置；列表页按此渲染）
  private volatile String activeSearchKeyword;

  private PostService() {
    // 私有构造方法，单例模式
  }

  public record LikeResult(boolean liked, int likeCount) {
  }

  public static PostService getInstance() {
    if (instance == null) {
      instance = new PostService();
    }
    return instance;
  }

  /**
   * 【新增】设置排序策略（供 PostListController 调用）
   */
  public void setSortStrategy(PostSortStrategy sortStrategy) {
    this.sortStrategy = (sortStrategy != null) ? sortStrategy : new TimeSortStrategy();
  }

  /**
   * 设置搜索策略（允许运行时切换）。
   */
  public void setSearchStrategy(PostSearchStrategy searchStrategy) {
    this.searchStrategy = (searchStrategy != null) ? searchStrategy : new TitleKeywordStrategy();
  }

  /**
   * 设置当前搜索关键字（为空则清除搜索态）。
   */
  public void setSearchKeyword(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      this.activeSearchKeyword = null;
    } else {
      this.activeSearchKeyword = keyword.trim();
    }
  }

  /**
   * 【新增】搜索帖子（供 MainLayoutController 调用）
   */
  public List<Post> searchPosts(String keyword) {
    setSearchKeyword(keyword);
    List<Post> allPosts = getAllPosts();
    return (searchStrategy != null) ? searchStrategy.search(allPosts, keyword) : allPosts;
  }

  /**
   * 列表页展示用：如果存在搜索关键字，则返回搜索结果；否则返回全量列表。
   */
  public List<Post> getVisiblePosts() {
    String keyword = this.activeSearchKeyword;
    if (keyword == null || keyword.isBlank()) {
      return getAllPosts();
    }
    List<Post> allPosts = getAllPosts();
    return (searchStrategy != null) ? searchStrategy.search(allPosts, keyword) : allPosts;
  }

  /**
   * 创建帖子：加入缓存并设置为已发布（供 PostEditorController 调用）。
   */
  public void createPost(Post post) {
    if (post == null) {
      throw new IllegalArgumentException("post must not be null");
    }
    ensureInitialized();

    // 确保帖子进入已发布状态
    if (post.getStatus() == PostStatus.DRAFT) {
      if (post.isAnonymous()) {
        post.publishAnonymously();
      } else {
        post.publish();
      }
    }

    // 新帖子放到缓存头部（后续仍会由排序策略重新排序）
    synchronized (cachedPosts) {
      cachedPosts.add(0, post);
    }

    persistBestEffort();
  }

  /**
   * 获取所有帖子（模拟数据）
   */
  public List<Post> getAllPosts() {
    ensureInitialized();

    // 返回前按当前策略排序（直接排序缓存列表，使 UI 侧引用保持一致）
    if (sortStrategy != null) {
      sortStrategy.sort(cachedPosts);
    }

    return cachedPosts;
  }

  private synchronized void ensureInitialized() {
    if (initialized) {
      return;
    }

    // Prefer loading from DAO
    DataLoader loader = this.dataLoader;
    if (loader == null) {
      loader = AppContext.getDataLoader();
      this.dataLoader = loader;
    }

    if (loader != null) {
      try {
        List<Post> loaded = loader.loadPosts();
        if (loaded != null && !loaded.isEmpty()) {
          cachedPosts.addAll(loaded);
          initialized = true;
          return;
        }
      } catch (Exception e) {
        LogUtil.warn("loadPosts failed, falling back to seeded posts: " + e.getMessage());
      }
    }

    // 模拟一些帖子数据（仅初始化一次，保证点赞/评论不会因重新加载而丢失）
    cachedPosts.add(Post.seeded("Java多线程学习心得", "最近在学习Java多线程编程，分享一些心得体会...",
      "student_001", PostCategory.LEARNING, 150, 45, 23, LocalDateTime.now().minusHours(2)));

    cachedPosts.add(Post.seeded("校园篮球比赛通知", "本周五下午体育馆举行篮球比赛，欢迎大家参加！",
      "sports_committee", PostCategory.CAMPUS_LIFE, 320, 120, 56,
      LocalDateTime.now().minusHours(5)));

    cachedPosts.add(
      Post.seeded("转让二手笔记本电脑", "联想ThinkPad，9成新，配置：i7/16G/512G SSD", "student_2024",
        PostCategory.SECOND_HAND, 180, 65, 12, LocalDateTime.now().minusDays(1)));

    cachedPosts.add(
      Post.seeded("周末编程学习小组招募", "寻找对Java开发感兴趣的同学一起学习交流", "tech_group",
        PostCategory.ACTIVITY, 95, 32, 18, LocalDateTime.now().minusDays(2)));

    cachedPosts.add(
      Post.seeded("关于宿舍网络的问题", "最近宿舍网络不太稳定，有相同情况的同学吗？", "student_net",
        PostCategory.QNA, 210, 78, 45, LocalDateTime.now().minusHours(10)));

    cachedPosts.add(
      Post.seeded("实习经验分享会", "本周六下午有学长学姐分享实习经验，欢迎参加", "career_center",
        PostCategory.EMPLOYMENT, 420, 200, 89, LocalDateTime.now().minusHours(1)));

    initialized = true;
  }

  private void persistBestEffort() {
    DataLoader loader = this.dataLoader;
    if (loader == null) {
      loader = AppContext.getDataLoader();
      this.dataLoader = loader;
    }
    if (loader == null) {
      return;
    }

    try {
      loader.savePosts(new ArrayList<>(cachedPosts));
    } catch (Exception e) {
      LogUtil.warn("savePosts failed (ignored): " + e.getMessage());
    }
  }

  private Post findPostById(String postId) {
    if (postId == null || postId.isBlank()) {
      return null;
    }
    ensureInitialized();
    for (Post p : cachedPosts) {
      if (postId.equals(p.getPostId())) {
        return p;
      }
    }
    return null;
  }

  /**
   * 点赞/取消点赞。
   * @return LikeResult(切换后是否已点赞, 最新点赞数)
   */
  public LikeResult toggleLike(String postId, String userId) {
    Post post = findPostById(postId);
    if (post == null) {
      return new LikeResult(false, 0);
    }
    boolean liked = post.toggleLike(userId);

    // NEW: persist likes change (best-effort)
    persistBestEffort();

    return new LikeResult(liked, post.getLikeCount());
  }

  /**
   * 添加评论：Controller 传入 Comment(包含内容/父评论等)，Service 负责持久化/业务处理。
   * 返回“最终保存”的 Comment 对象（用于 UI 直接追加）。
   */
  public Comment addComment(String postId, Comment comment) {
    Post post = findPostById(postId);
    if (post == null || comment == null) {
      return null;
    }

    int beforeSize = post.getComments().size();
    User currentUser = UserSession.getInstance().getCurrentUser();

    Comment saved;
    if (currentUser != null) {
      // 使用 Post 的业务方法，确保触发通知等副作用
      post.addComment(currentUser, comment.getContent());
      List<Comment> after = post.getComments();
      if (after.size() > beforeSize) {
        saved = after.get(after.size() - 1);
      } else {
        saved = null;
      }
    } else {
      // 无登录态：仅追加数据
      post.addComment(comment);
      saved = comment;
    }

    // NEW: persist comment change (best-effort)
    if (saved != null) {
      persistBestEffort();
    }

    return saved;
  }

  /**
   * 获取指定分类的帖子
   */
  public List<Post> getPostsByCategory(PostCategory category) {
    List<Post> allPosts = getAllPosts();
    List<Post> filteredPosts = new ArrayList<>();

    for (Post post : allPosts) {
      if (post.getCategory() == category) {
        filteredPosts.add(post);
      }
    }

    return filteredPosts;
  }

  /**
   * 获取热门帖子（热度大于20）
   */
  public List<Post> getHotPosts() {
    List<Post> allPosts = getAllPosts();
    List<Post> hotPosts = new ArrayList<>();

    for (Post post : allPosts) {
      if (post.calculateHotScore() > 20.0) {
        hotPosts.add(post);
      }
    }

    return hotPosts;
  }

  /**
   * 刷新帖子列表（在实际项目中这里会从数据库重新加载）
   */
  public List<Post> refreshPosts() {
    // 模拟刷新操作，返回最新的帖子列表
    return getAllPosts();
  }

  /** 添加评论到帖子（简化实现） */
  public static boolean addComment(Post post, Comment comment) {
    if (post == null || comment == null)
      return false;
    post.addComment(new Student(comment.getAuthorId(), "匿名", "pass"), comment.getContent());
    return true;
  }
}
