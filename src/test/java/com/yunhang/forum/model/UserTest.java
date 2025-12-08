package com.yunhang.forum.model;

import com.yunhang.forum.model.entity.*;
import com.yunhang.forum.model.session.UserSession;

import java.util.Map;

public class UserTest {
    public static void main(String[] args) {

        Student studentA = new Student("23371054", "小明", "password123");
        Student studentB = new Student("23373244", "李华", "password456");
        Admin admin = new Admin("22371065", "管理员 朱迪", "password789");

        System.out.println("当前登录用户: " + (UserSession.getInstance().isLoggedIn() ?
                UserSession.getInstance().getCurrentUser().getNickname() : "无"));

        studentA.login("password456");//使用错误密码登录

        studentA.login("password123"); // 登录成功

        //验证单例模式
        User currentUser = UserSession.getInstance().getCurrentUser();
        System.out.println("当前用户是 " + currentUser.getNickname());


        currentUser.publishPost(new Post("我的第一篇帖子。"));
        currentUser.comment("post-id-1", "写得真好！");

        System.out.println("小明发布的帖子数量: " + currentUser.getPublishedPosts().size());

        // 5. 登出
        UserSession.getInstance().clearSession();

        UserSession.getInstance().startSession(studentB);

        // 李华举报小明
        ((Student)UserSession.getInstance().getCurrentUser()).reportUser(studentA.getStudentID(), "小明帖子内容有争议");
        //管理员登录
        admin.login("password789");
        Map<String,Report> reports = admin.reviewReports();
        if (!reports.isEmpty()) {
            System.out.println("收到举报数: " + reports.size());
            for (Report report : reports.values()) {
                System.out.print("管理员封禁被举报者: ");
                admin.banUser(report.targetId, 7);
            }

        }
        //密码更新
        admin.updatePassword("password123","password101");
        admin.updatePassword("password789", "password101");

        //验证新密码
        System.out.print("尝试使用旧密码登录: ");
        admin.login("password789"); // 应该失败

        System.out.print("尝试使用新密码登录: ");
        admin.login("password101"); // 应该成功
        UserSession.getInstance().clearSession();
    }
}
