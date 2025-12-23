package com.yunhang.forum.util;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.model.entity.GlobalVariables;

import com.yunhang.forum.service.EmailService;

import java.util.*;

public class UserService {

    private final DataLoader dataLoader;
    private final EmailService emailService;

    public UserService() {
        this(AppContext.getDataLoader(), new EmailService());
    }

    public UserService(DataLoader dataLoader, EmailService emailService) {
        this.dataLoader = dataLoader;
        this.emailService = (emailService != null) ? emailService : new EmailService();

        // CRITICAL: load persisted users into memory on service initialization
        loadUsersIntoMemory();
    }

    private void loadUsersIntoMemory() {
        if (dataLoader == null) {
            LogUtil.warn("UserService init: DataLoader is null; users will not be loaded");
            return;
        }

        try {
            List<User> users = dataLoader.loadUsers();
            if (users == null || users.isEmpty()) {
                return;
            }

            // De-duplicate by studentID and populate GlobalVariables.userMap
            for (User u : users) {
                if (u == null) continue;
                String sid = u.getStudentID();
                if (sid == null || sid.isBlank()) continue;
                GlobalVariables.userMap.put(sid, u);
            }
        } catch (Exception e) {
            LogUtil.error("UserService init: loadUsers failed", e);
        }
    }


    private User findUserById(String id) {
        if (id == null) {
            return null;
        }
        // GlobalVariables.userMap is keyed by studentId
        return GlobalVariables.userMap.get(id);
    }


    public boolean login(String studentId, String password) {
        User user = findUserById(studentId);

        if (user == null) {
            return false;
        }
        if (user.verifyPassword(password)) {
            UserSession.getInstance().startSession(user);
            return true;
        } else {
            return false;
        }
    }


    public boolean registerStudent(String studentId, String nickname, String password) {
        if (isStudentIdExists(studentId)) {
            LogUtil.warn("注册失败：学号已存在。 studentId=" + studentId);
            return false;
        }

        for (User user : GlobalVariables.userMap.values()) {
            if (nickname.equals(user.getNickname())) {
                LogUtil.warn("注册失败：昵称已存在。 nickname=" + nickname);
                return false;
            }
        }

        Student newUser = new Student(studentId, nickname, password);

        // Ensure consistent key and avoid duplicate insertion
        GlobalVariables.userMap.put(studentId, newUser);
        LogUtil.info("新用户 [" + newUser.getNickname() + "] 注册成功。");

        // Persist via DAO (best-effort)
        if (dataLoader != null) {
            // Persist de-duplicated list (by studentID)
            Map<String, User> byStudentId = new LinkedHashMap<>();
            for (User u : GlobalVariables.userMap.values()) {
                if (u != null && u.getStudentID() != null && !u.getStudentID().isBlank()) {
                    byStudentId.put(u.getStudentID(), u);
                }
            }

            boolean ok = dataLoader.saveUsers(new ArrayList<>(byStudentId.values()));
            if (!ok) {
                LogUtil.warn("用户数据持久化失败（忽略）：saveUsers 返回 false");
            }
        }
        return true;
    }

    /**
     * 发送验证码逻辑
     */
    public boolean sendVerificationCode(String email) {
        return emailService.sendVerificationCode(email);
    }

    public boolean isVerificationCodeValid(String email, String code) {
        return emailService.verifyCode(email, code);
    }


    public boolean isStudentIdExists(String studentId) {
        return findUserById(studentId) != null;
    }

    public static boolean isSmtpConfigured() {
        return EmailService.isSmtpConfigured();
    }

    public static String smtpConfigHelp() {
        return EmailService.smtpConfigHelp();
    }

    public List<Post> getUserPosts(String studentId) {
        User user = GlobalVariables.userMap.get(studentId);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Post> userPosts = user.getPublishedPosts();
        userPosts.sort((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime()));
        return userPosts;
    }
}
