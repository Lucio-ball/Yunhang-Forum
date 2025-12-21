package com.yunhang.forum.service.strategy.impl;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostSearchStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实现简单的模糊查询：过滤出标题中包含 keyword 的帖子 (忽略大小写)
 */
public class TitleKeywordStrategy implements PostSearchStrategy {
  @Override
  public List<Post> search(List<Post> source, String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return source;
    }

    final String lowerCaseKeyword = keyword.trim().toLowerCase();

    // 使用 Java Stream API 实现过滤
    return source.stream().filter(
            post -> post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerCaseKeyword))
        .collect(Collectors.toList());
  }
}
