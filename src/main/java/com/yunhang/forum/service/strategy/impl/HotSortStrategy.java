package com.yunhang.forum.service.strategy.impl;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostSortStrategy;

import java.util.Comparator;
import java.util.List;

/**
 * 实现按 hotScore (热度) 倒序排列
 */
public class HotSortStrategy implements PostSortStrategy {
  @Override
  public void sort(List<Post> posts) {
    // 确保是倒序（热度最高的在前面）
    posts.sort(Comparator.comparing(Post::getHotScore).reversed());
  }
}
