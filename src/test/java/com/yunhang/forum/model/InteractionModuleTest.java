// filepath: /Users/xigma/IdeaProjects/Yunhang-Forum/src/test/java/com/yunhang/forum/model/InteractionModuleTest.java
package com.yunhang.forum.model;

import com.yunhang.forum.model.entity.*;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示：当调用 post.addComment(comment) 时，自动为作者生成 Notification。
 */
public class InteractionModuleTest {

    static class DummyStudent extends Student {
        public DummyStudent(String studentID, String nickname, String password) {
            super(studentID, nickname, password);
        }
    }

    @Test
    public void testCommentTriggersNotification() {
        // 准备用户与帖子
        DummyStudent author = new DummyStudent("20250001", "作者A", "pass");
        DummyStudent commenter = new DummyStudent("20250002", "评论者B", "pass");

        // IMPORTANT: Post.authorId 在业务中以 studentID 作为统一标识
        Post post = new Post("标题", "正文", author.getStudentID(), PostCategory.LEARNING);
        post.safeTransitionTo(PostStatus.PUBLISHED);

        // 断言：初始无通知
        assertEquals(0, author.getNotifications().size());

        // 动作：发表评论
        post.addComment(commenter, "支持一下！");

        // 验证：作者收到通知
        List<com.yunhang.forum.model.entity.Notification> notifications = author.getNotifications();
        assertEquals(1, notifications.size());
        com.yunhang.forum.model.entity.Notification n = notifications.get(0);
        assertEquals("新评论提醒", n.getTitle());
        assertTrue(n.getContent().contains("评论者B"));
        assertTrue(n.getContent().contains("标题"));
    }
}
