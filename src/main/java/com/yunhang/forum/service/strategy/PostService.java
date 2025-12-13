package com.yunhang.forum.service.strategy;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子服务类 - 处理帖子相关的业务逻辑
 */
public class PostService {

  // 模拟数据，实际项目中应从DAO层获取
  public List<Post> getAllPosts() {
    List<Post> posts = new ArrayList<>();

    // 创建示例帖子
    Post post1 = new Post(
        "Java多线程编程实践",
        "最近在学习Java多线程编程，分享一下线程池的使用经验...\n" +
            "ExecutorService是一个很好的工具，可以管理线程生命周期。",
        "student_001",
        PostCategory.LEARNING
    );
    post1.publish();
    post1.setPublishTime(LocalDateTime.now().minusHours(2));
    post1.setViewCount(150);
    post1.setLikeCount(25);
    post1.setCommentCount(12);

    Post post2 = new Post(
        "校园篮球赛招募队员",
        "本周五下午3点在体育馆举行校园篮球赛，现招募队员...\n" +
            "要求：有一定篮球基础，团队意识强。",
        "sport_club",
        PostCategory.ACTIVITY
    );
    post2.publish();
    post2.setPublishTime(LocalDateTime.now().minusHours(5));
    post2.setViewCount(230);
    post2.setLikeCount(45);
    post2.setCommentCount(28);

    Post post3 = new Post(
        "转让二手教材《数据结构》",
        "9成新《数据结构（C语言版）》，原价68元，现价30元...\n" +
            "几乎没写过，需要的同学请联系。",
        "user_2024",
        PostCategory.SECOND_HAND
    );
    post3.publish();
    post3.setPublishTime(LocalDateTime.now().minusDays(1));
    post3.setViewCount(89);
    post3.setLikeCount(12);
    post3.setCommentCount(8);

    Post post4 = new Post(
        "求推荐Java学习资料",
        "大家好，我是编程新手，想学习Java...\n" +
            "请大家推荐一些适合初学者的学习资料和视频教程。",
        "newbie_coder",
        PostCategory.QNA
    );
    post4.publish();
    post4.setPublishTime(LocalDateTime.now().minusDays(2));
    post4.setViewCount(320);
    post4.setLikeCount(56);
    post4.setCommentCount(42);

    Post post5 = new Post(
        "校园美食节通知",
        "下周三在食堂广场举办校园美食节...\n" +
            "各社团将展示特色美食，欢迎同学们前来品尝！",
        "campus_admin",
        PostCategory.CAMPUS_LIFE
    );
    post5.publish();
    post5.setPublishTime(LocalDateTime.now().minusHours(10));
    post5.setViewCount(180);
    post5.setLikeCount(38);
    post5.setCommentCount(22);

    // 添加更多示例帖子
    posts.add(post1);
    posts.add(post2);
    posts.add(post3);
    posts.add(post4);
    posts.add(post5);

    return posts;
  }

  /**
   * 根据分类获取帖子
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
      if (post.isHot()) {
        hotPosts.add(post);
      }
    }

    return hotPosts;
  }

  /**
   * 刷新帖子数据（模拟）
   */
  public List<Post> refreshPosts() {
    // 在实际项目中，这里会重新从数据库加载数据
    // 这里返回新的模拟数据以模拟刷新
    List<Post> newPosts = new ArrayList<>(getAllPosts());

    // 添加一个新的帖子模拟刷新效果
    Post newPost = new Post(
        "📢 新通知：图书馆延长开放时间",
        "为满足同学们的学习需求，图书馆决定从下周起...\n" +
            "开放时间延长至晚上11点，请大家合理安排时间。",
        "library_admin",
        PostCategory.ANNOUNCEMENT
    );
    newPost.publish();
    newPost.setPublishTime(LocalDateTime.now().minusMinutes(30));
    newPost.setViewCount(50);
    newPost.setLikeCount(15);
    newPost.setCommentCount(5);

    newPosts.add(0, newPost); // 添加到列表开头

    return newPosts;
  }
}