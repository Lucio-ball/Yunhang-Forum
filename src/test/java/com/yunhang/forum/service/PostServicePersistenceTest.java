package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.AppContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression: ensure interaction operations (like/comment) are persisted via DataLoader.savePosts.
 */
public class PostServicePersistenceTest {

  private static final class InMemoryDataLoader implements DataLoader {
    final AtomicInteger savePostsCalls = new AtomicInteger();
    final AtomicInteger saveUsersCalls = new AtomicInteger();

    List<Post> posts = new ArrayList<>();

    @Override
    public List<com.yunhang.forum.model.entity.User> loadUsers() {
      return new ArrayList<>();
    }

    @Override
    public boolean saveUsers(List<com.yunhang.forum.model.entity.User> users) {
      saveUsersCalls.incrementAndGet();
      return true;
    }

    @Override
    public List<Post> loadPosts() {
      return new ArrayList<>(posts);
    }

    @Override
    public boolean savePosts(List<Post> posts) {
      savePostsCalls.incrementAndGet();
      this.posts = new ArrayList<>(posts);
      return true;
    }
  }

  @AfterEach
  void cleanupSession() {
    UserSession.getInstance().clearSession();
  }

  @Test
  void toggleLikeAndAddCommentShouldPersist() {
    InMemoryDataLoader loader = new InMemoryDataLoader();
    AppContext.setDataLoader(loader);

    // Prepare a logged-in user
    Student user = new Student("20251234", "tester", "pass");
    UserSession.getInstance().startSession(user);

    // Create a post and persist once on create
    Post post = new Post("t", "c", user.getStudentID(), PostCategory.LEARNING);
    PostService.getInstance().createPost(post);

    int saveAfterCreate = loader.savePostsCalls.get();
    assertTrue(saveAfterCreate >= 1, "createPost should persist at least once");

    // Like should persist
    PostService.getInstance().toggleLike(post.getPostId(), user.getStudentID());
    assertTrue(loader.savePostsCalls.get() > saveAfterCreate, "toggleLike should persist");

    int saveAfterLike = loader.savePostsCalls.get();

    // Comment should persist
    Comment cmt = new Comment(post.getPostId(), user.getStudentID(), null, "hello");
    Comment saved = PostService.getInstance().addComment(post.getPostId(), cmt);
    assertNotNull(saved);
    assertTrue(loader.savePostsCalls.get() > saveAfterLike, "addComment should persist");
  }
}

