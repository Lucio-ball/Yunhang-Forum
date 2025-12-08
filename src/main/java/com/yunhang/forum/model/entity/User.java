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
    protected final String userID;
    protected final String studentID;
    protected String nickname;
    protected String avatarPath;
    protected final LocalDateTime registrationTime;
    private String hashedPasswordPBKDF2;
    private String salt;
    private final int ITERATIONS = 50; // 迭代次数
    private static final int KEY_LENGTH = 256; // 派生密钥长度
    protected List<Post> myPosts = new ArrayList<>();

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
        System.out.println(this.nickname + " 发布了帖子: " + post.content);
        return true;
    }

    public boolean comment(String postId, String content) {
        System.out.println(this.nickname + " 评论了帖子 " + postId + ": " + content);
        return true;
    }

    public boolean upvote(String contentId) {
        System.out.println(this.nickname + " 点赞了 " + contentId);
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
            System.out.println("密码更新成功。");
            return true;
        } else {
            System.out.println("旧密码错误");
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
            e.printStackTrace();
            return null;
        }
    }

    public String getNickname() { return nickname; }
    public String getStudentID() { return studentID; }
}
