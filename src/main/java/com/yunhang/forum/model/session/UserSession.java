package com.yunhang.forum.model.session;


import com.yunhang.forum.model.entity.*;
import com.yunhang.forum.util.LogUtil;

public class UserSession {
    private static volatile UserSession instance;
    private volatile User currentUser;
    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public void startSession(User user) {
        this.currentUser = user;
        LogUtil.info(user.getNickname() + " 已登录。");
    }

    public void clearSession() {
        if (this.currentUser != null) {
            LogUtil.info(this.currentUser.getNickname() + " 已登出。");
        }
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    public void endSession() {
        this.currentUser = null;
        LogUtil.info("已退出登录");
    }

    // 与类图命名对齐的别名
    public void login(User user) {
        startSession(user);
    }

    public void logout() {
        clearSession();
    }
}
