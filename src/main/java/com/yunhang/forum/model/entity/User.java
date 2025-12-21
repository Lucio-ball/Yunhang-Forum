package com.yunhang.forum.model.entity;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public abstract class User {
    private final String userID;
    private final String studentID;
    private String nickname;
    private String avatarPath;
    private final LocalDateTime registrationTime;
    private String hashedPasswordPBKDF2;
    private String salt;
    private final int ITERATIONS = 50; // 迭代次数
    private static final int KEY_LENGTH = 256; // 派生密钥长度
    private final List<Post> myPosts = new ArrayList<>();
    // 新增：用户通知列表
    private final List<com.yunhang.forum.model.entity.Notification> notifications = new ArrayList<>();

    public User(String studentID, String nickname, String Password) {
        this.userID = UUID.randomUUID().toString();
        this.studentID = studentID;
        this.nickname = nickname;
        this.registrationTime = LocalDateTime.now();
        this.avatarPath = "avatar.png";
        securelyStorePassword(Password);
        GlobalVariables.userMap.put(userID, this);
    }

    public abstract boolean login(String password);

    public boolean publishPost(Post post) {
        myPosts.add(post);
        com.yunhang.forum.util.LogUtil.info(this.nickname + " 发布了帖子: " + post.getContent());
        return true;
    }

    public boolean comment(String postId, String content) {
        com.yunhang.forum.util.LogUtil.info(this.nickname + " 评论了帖子 " + postId + ": " + content);
        return true;
    }

    public boolean upvote(String contentId) {
        com.yunhang.forum.util.LogUtil.info(this.nickname + " 点赞了 " + contentId);
        return true;
    }

    public List<Post> getPublishedPosts() {
        return this.myPosts;
    }

    public final boolean verifyPassword(String inputPass) {
        String inputHash = hashPassword(inputPass, this.salt);
        return inputHash != null && inputHash.equals(this.hashedPasswordPBKDF2);
    }

    public boolean updatePassword(String oldPass, String newPass) {
        if (verifyPassword(oldPass)) {
            securelyStorePassword(newPass);
            com.yunhang.forum.util.LogUtil.info("密码更新成功。");
            return true;
        } else {
            com.yunhang.forum.util.LogUtil.warn("旧密码错误");
            return false;
        }
    }

    private void securelyStorePassword(String plainPassword) {
        this.salt = generateSalt();
        this.hashedPasswordPBKDF2 = hashPassword(plainPassword, this.salt); // 2. 加密
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hashPassword(String password, String saltStr) {
        try {
            char[] chars = password.toCharArray();
            byte[] saltBytes = Base64.getDecoder().decode(saltStr);

            PBEKeySpec spec = new PBEKeySpec(chars, saltBytes, this.ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            com.yunhang.forum.util.LogUtil.error("Password hashing failed", e);
            return null;
        }
    }

    public String getNickname() { return nickname; }
    public String getStudentID() { return studentID; }
    // 新增：与类图一致的命名别名
    public String getUserId() { return userID; }
    public String getUserID() { return userID; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }

    public String getAvatarPath() { return avatarPath; }
    // 新增：Profile 更新接口（类图要求）
    public void updateProfile(String nick, String avatar) {
        this.nickname = nick;
        this.avatarPath = avatar;
    }

    // 新增：通知相关 API
    /**
     * 为当前用户添加一条通知。
     * 关键逻辑：由可观察实体在事件触发时调用。
     */
    public void addNotification(com.yunhang.forum.model.entity.Notification notification) {
        if (notification != null) {
            notifications.add(notification);
        }
    }

    /** 获取用户的全部通知（快照拷贝）。 */
    public List<com.yunhang.forum.model.entity.Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }
}
