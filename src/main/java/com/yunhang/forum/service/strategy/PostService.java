package com.yunhang.forum.service.strategy;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子服务类 - 处理帖子相关的业务逻辑
 */
public class PostService {

  private static PostService instance;

  private PostService() {
    // 私有构造方法，单例模式
  }

  public static PostService getInstance() {
    if (instance == null) {
      instance = new PostService();
    }
    return instance;
  }

  /**
   * 获取所有帖子（模拟数据）
   */
  public List<Post> getAllPosts() {
    List<Post> posts = new ArrayList<>();

    // 模拟一些帖子数据
    posts.add(createPost("Java多线程学习心得",
        "最近在学习Java多线程编程，分享一些心得体会...",
        "student_001",
        PostCategory.LEARNING,
        150, 45, 23,
        LocalDateTime.now().minusHours(2)));

    posts.add(createPost("校园篮球比赛通知",
        "本周五下午体育馆举行篮球比赛，欢迎大家参加！",
        "sports_committee",
        PostCategory.CAMPUS_LIFE,
        320, 120, 56,
        LocalDateTime.now().minusHours(5)));

    posts.add(createPost("转让二手笔记本电脑",
        "联想ThinkPad，9成新，配置：i7/16G/512G SSD",
        "student_2024",
        PostCategory.SECOND_HAND,
        180, 65, 12,
        LocalDateTime.now().minusDays(1)));

    posts.add(createPost("周末编程学习小组招募",
        "寻找对Java开发感兴趣的同学一起学习交流",
        "tech_group",
        PostCategory.ACTIVITY,
        95, 32, 18,
        LocalDateTime.now().minusDays(2)));

    posts.add(createPost("关于宿舍网络的问题",
        "最近宿舍网络不太稳定，有相同情况的同学吗？",
        "student_net",
        PostCategory.QNA,
        210, 78, 45,
        LocalDateTime.now().minusHours(10)));

    posts.add(createPost("实习经验分享会",
        "本周六下午有学长学姐分享实习经验，欢迎参加",
        "career_center",
        PostCategory.EMPLOYMENT,
        420, 200, 89,
        LocalDateTime.now().minusHours(1)));

    return posts;
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

  /**
   * 创建帖子（辅助方法）
   */
  private Post createPost(String title, String content, String authorId,
      PostCategory category, int views, int likes, int comments,
      LocalDateTime publishTime) {
    Post post = new Post(title, content, authorId, category);
    post.setPublishTime(publishTime);
    post.setViewCount(views);
    post.setLikeCount(likes);
    post.setCommentCount(comments);
    post.publish(); // 设置为已发布状态
    return post;
  }
}
