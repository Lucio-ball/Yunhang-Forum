package com.yunhang.forum.model.session;


import com.yunhang.forum.model.entity.*;

public class UserSession {
    private static volatile UserSession instance;
    private User currentUser;
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
        System.out.println(user.getNickname() + " 已登录。");
    }

    public void clearSession() {
        if (this.currentUser != null) {
            System.out.println(this.currentUser.getNickname() + " 已登出。");
        }
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
