package com.yunhang.forum.model.entity;

import com.yunhang.forum.model.session.UserSession;

public class Student extends User {

    public Student(String studentID, String nickname, String password) {
        super(studentID, nickname, password);
        GlobalVariables.studentMap.put(getUserID(), this);
    }

    @Override
    public boolean login(String password) {
        boolean success = this.verifyPassword(password);
        if (success) {
            com.yunhang.forum.util.LogUtil.info("[" + this.getNickname() + "] 登录成功。");
            UserSession.getInstance().startSession(this);
        } else {
            com.yunhang.forum.util.LogUtil.warn("登录失败：密码错误。");
        }
        return success;
    }
    public void reportUser(String targetId, String reason) {
        Report newReport = new Report(targetId, reason);
        GlobalVariables.reportMap.put(newReport.getTargetId(),newReport);
        if(GlobalVariables.reportMap.containsKey(newReport.getTargetId())) {
            com.yunhang.forum.util.LogUtil.info("举报成功");
        }
        else {
            com.yunhang.forum.util.LogUtil.warn("举报失败，请重新尝试");
        }
    }

    // 与类图对齐：新增 like(postId)
    public void like(String postId) {
        this.upvote(postId);
    }
}
