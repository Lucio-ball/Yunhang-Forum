package com.yunhang.forum.service.strategy.impl;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostSortStrategy;

import java.util.Comparator;
import java.util.List;

/**
 * 实现按 publishTime 倒序排列（最新的在前面）
 */
public class TimeSortStrategy implements PostSortStrategy {
  @Override
  public void sort(List<Post> posts) {
    // 使用 Java 8+ 的 Comparator.comparing(...) 语法
    posts.sort(Comparator.comparing(Post::getPublishTime).reversed());
  }
}
