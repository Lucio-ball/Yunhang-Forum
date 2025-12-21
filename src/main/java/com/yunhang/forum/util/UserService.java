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

    UserService(DataLoader dataLoader, EmailService emailService) {
        this.dataLoader = dataLoader;
        this.emailService = (emailService != null) ? emailService : new EmailService();
    }


    private User findUserById(String id) {
        for (User user : GlobalVariables.userMap.values()) {
            if (user.getStudentID().equals(id))
                return user;
        }
        return null;
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
        GlobalVariables.userMap.put(studentId, newUser);
        LogUtil.info("新用户 [" + newUser.getNickname() + "] 注册成功。");

        // Persist via DAO (best-effort)
        if (dataLoader != null) {
            boolean ok = dataLoader.saveUsers(new ArrayList<>(GlobalVariables.userMap.values()));
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
        List<Post> userPosts = new ArrayList<>();
        User user = GlobalVariables.userMap.get(studentId);
        userPosts = user.getPublishedPosts();
        userPosts.sort((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime()));
        return userPosts;
    }
}
