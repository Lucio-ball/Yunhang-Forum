package com.yunhang.forum.model.entity;

import java.util.Map;
import com.yunhang.forum.model.session.UserSession;

public class Admin extends User {

    public Admin(String studentID, String nickname, String password) {
        super(studentID, nickname, password);
        GlobalVariables.adminMap.put(userID, this);
    }

    @Override
    public boolean login(String password) {
        boolean success = this.verifyPassword(password);
        if (success) {
            System.out.println("[" + this.nickname + "] 登录成功。");
            UserSession.getInstance().startSession(this);
        } else {
            System.out.println("登录失败。");
        }
        return success;
    }

    public boolean deletePost(String postId, String reason) {
        System.out.println("管理员删除了帖子 " + postId + "，原因: " + reason);
        return true;
    }

    public boolean banUser(String studentId, int durationDay) {
        System.out.println("管理员封禁了用户 " + studentId + " " + durationDay + " 天。");
        return true;
    }

    public Map<String,Report> reviewReports() {
        if (GlobalVariables.reportMap.isEmpty()) {
            System.out.println("目前没有收到任何举报。");
        }
        return GlobalVariables.reportMap;
    }
}
