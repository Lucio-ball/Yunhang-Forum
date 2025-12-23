package com.yunhang.forum.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken; // **处理泛型擦除的关键类**
import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.Admin;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class JsonDataLoader implements DataLoader {

  private static final String USER_FILE_PATH = "data/users.json";
  private static final String POST_FILE_PATH = "data/posts.json";

  private final Gson gson;
  private volatile boolean enabled = true;

  public JsonDataLoader() {
    // 使用 GsonBuilder 初始化 Gson
    this.gson = new GsonBuilder()
        .registerTypeAdapter(User.class, new UserPolymorphicAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();

    // 确保 data 文件夹和 json 文件存在
    initFile(USER_FILE_PATH);
    initFile(POST_FILE_PATH);
  }

  /**
   * Gson adapter to support abstract User (Student/Admin) serialization/deserialization.
   * Writes a discriminator field "_type".
   */
  private static final class UserPolymorphicAdapter implements JsonSerializer<User>, JsonDeserializer<User> {
    private static final String TYPE_FIELD = "_type";
    private static final String TYPE_STUDENT = "Student";
    private static final String TYPE_ADMIN = "Admin";

    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
      if (src == null) {
        return null;
      }
      JsonElement delegated = context.serialize(src, src.getClass());
      if (!delegated.isJsonObject()) {
        return delegated;
      }

      JsonObject obj = delegated.getAsJsonObject();
      obj.addProperty(TYPE_FIELD, src.getClass().getSimpleName());
      return obj;
    }

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      if (json == null || json.isJsonNull()) {
        return null;
      }
      if (!json.isJsonObject()) {
        throw new JsonParseException("User must be a JSON object");
      }

      JsonObject obj = json.getAsJsonObject();
      String type = null;
      if (obj.has(TYPE_FIELD) && obj.get(TYPE_FIELD).isJsonPrimitive()) {
        type = obj.get(TYPE_FIELD).getAsString();
      }

      Class<? extends User> clazz;
      if (TYPE_ADMIN.equals(type)) {
        clazz = Admin.class;
      } else if (TYPE_STUDENT.equals(type)) {
        clazz = Student.class;
      } else {
        // Backward compatibility: old JSON may not have _type
        // Heuristic: adminMap/studentMap are not serialized; use studentID presence as default
        clazz = Student.class;
      }

      return context.deserialize(obj, clazz);
    }
  }

  /**
   * Stable ISO serialization for LocalDateTime.
   * Avoids Gson reflective / internal representation issues.
   */
  private static final class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
      return (src == null) ? null : new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      if (json == null || json.isJsonNull()) {
        return null;
      }
      if (!json.isJsonPrimitive()) {
        throw new JsonParseException("LocalDateTime must be a JSON primitive");
      }
      try {
        return LocalDateTime.parse(json.getAsString());
      } catch (DateTimeParseException e) {
        throw new JsonParseException("Invalid LocalDateTime: " + json.getAsString(), e);
      }
    }
  }

  private void initFile(String pathStr) {
    Path path = Paths.get(pathStr);
    try {
      if (path.getParent() != null) {
        Files.createDirectories(path.getParent()); // 创建 data 文件夹
      }
      if (!Files.exists(path)) {
        Files.writeString(path, "[]"); // 如果文件不存在，创建空 JSON 列表 "[]"
        com.yunhang.forum.util.LogUtil.info("Initialized data file: " + pathStr);
      }
    } catch (IOException e) {
      enabled = false;
      com.yunhang.forum.util.LogUtil.error("Failed to initialize data file (persistence disabled): " + pathStr, e);
    }
  }

  @Override
  public List<User> loadUsers() {
    if (!enabled) {
      com.yunhang.forum.util.LogUtil.warn("Persistence disabled: loadUsers returns empty list");
      return new ArrayList<>();
    }

    try (Reader reader = new FileReader(USER_FILE_PATH)) {
      Type userListType = new TypeToken<ArrayList<User>>() {
      }.getType();
      List<User> users = gson.fromJson(reader, userListType);
      return users != null ? users : new ArrayList<>();
    } catch (FileNotFoundException e) {
      // Should not happen due to initFile, but keep it safe
      com.yunhang.forum.util.LogUtil.warn("users.json not found; returning empty list");
      return new ArrayList<>();
    } catch (Exception e) {
      // CRITICAL: surface parse errors - returning empty would look like data loss
      com.yunhang.forum.util.LogUtil.error("loadUsers failed (parse or IO error)", e);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean saveUsers(List<User> users) {
    if (!enabled) {
      com.yunhang.forum.util.LogUtil.warn("Persistence disabled: saveUsers ignored");
      return false;
    }

    List<User> safeUsers = (users != null) ? users : new ArrayList<>();

    try (Writer writer = new FileWriter(USER_FILE_PATH)) {
      gson.toJson(safeUsers, writer);
      writer.flush();
      return true;
    } catch (Exception e) {
      com.yunhang.forum.util.LogUtil.error("saveUsers failed", e);
      return false;
    }
  }

  @Override
  public List<Post> loadPosts() {
    if (!enabled) {
      com.yunhang.forum.util.LogUtil.warn("Persistence disabled: loadPosts returns empty list");
      return new ArrayList<>();
    }
    try (Reader reader = new FileReader(POST_FILE_PATH)) {
      Type postListType = new TypeToken<ArrayList<Post>>() {
      }.getType();
      List<Post> posts = gson.fromJson(reader, postListType);
      return posts != null ? posts : new ArrayList<>();
    } catch (IOException e) {
      com.yunhang.forum.util.LogUtil.error("loadPosts failed", e);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean savePosts(List<Post> posts) {
    if (!enabled) {
      com.yunhang.forum.util.LogUtil.warn("Persistence disabled: savePosts ignored");
      return false;
    }
    try (Writer writer = new FileWriter(POST_FILE_PATH)) {
      gson.toJson(posts, writer);
      return true;
    } catch (IOException e) {
      com.yunhang.forum.util.LogUtil.error("savePosts failed", e);
      return false;
    }
  }
}
