package com.yunhang.forum.model.entity;

import java.util.Map;
import com.yunhang.forum.model.session.UserSession;

public class Admin extends User {

    public Admin(String studentID, String nickname, String password) {
        super(studentID, nickname, password);
        GlobalVariables.adminMap.put(getUserID(), this);
    }

    @Override
    public boolean login(String password) {
        boolean success = this.verifyPassword(password);
        if (success) {
            com.yunhang.forum.util.LogUtil.info("[" + this.getNickname() + "] 登录成功。");
            UserSession.getInstance().startSession(this);
        } else {
            com.yunhang.forum.util.LogUtil.warn("登录失败。");
        }
        return success;
    }

    public boolean deletePost(String postId, String reason) {
        com.yunhang.forum.util.LogUtil.info("管理员删除了帖子 " + postId + "，原因: " + reason);
        return true;
    }

    public boolean banUser(String studentId, int durationDay) {
        com.yunhang.forum.util.LogUtil.info("管理员封禁了用户 " + studentId + " " + durationDay + " 天。");
        return true;
    }

    public Map<String,Report> reviewReports() {
        if (GlobalVariables.reportMap.isEmpty()) {
            com.yunhang.forum.util.LogUtil.info("目前没有收到任何举报。");
        }
        return GlobalVariables.reportMap;
    }

    // 与类图对齐的便捷重载
    public void deletePost(String postId) {
        deletePost(postId, "");
    }

    public void banUser(String userId) {
        banUser(userId, 0);
    }
}
