package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.entity.GlobalVariables;
import com.yunhang.forum.util.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression: verify that UserService loads persisted users through DataLoader on construction.
 */
public class UserPersistenceSmokeTest {

  private static final class InMemoryUserLoader implements DataLoader {
    private final List<User> usersToLoad;

    InMemoryUserLoader(List<User> usersToLoad) {
      this.usersToLoad = usersToLoad;
    }

    @Override
    public List<User> loadUsers() {
      return new ArrayList<>(usersToLoad);
    }

    @Override
    public boolean saveUsers(List<User> users) {
      return true;
    }

    @Override
    public List<com.yunhang.forum.model.entity.Post> loadPosts() {
      return new ArrayList<>();
    }

    @Override
    public boolean savePosts(List<com.yunhang.forum.model.entity.Post> posts) {
      return true;
    }
  }

  @Test
  void constructorShouldLoadUsersIntoGlobalMap() {
    GlobalVariables.userMap.clear();

    Student persisted = new Student("24373309", "xigma", "pass");
    // mimic persisted object: ensure in-memory map cleared after creation to simulate fresh start
    GlobalVariables.userMap.clear();

    DataLoader loader = new InMemoryUserLoader(List.of(persisted));
    UserService service = new UserService(loader, null);

    assertTrue(service.isStudentIdExists("24373309"));
    assertTrue(service.login("24373309", "pass"));
  }
}
