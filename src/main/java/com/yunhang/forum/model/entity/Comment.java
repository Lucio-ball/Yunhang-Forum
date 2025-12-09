// filepath: /Users/xigma/IdeaProjects/Yunhang-Forum/src/main/java/com/yunhang/forum/model/entity/Comment.java
package com.yunhang.forum.model.entity;

import com.yunhang.forum.model.enums.EventType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 评论实体，支持楼中楼（通过 parentId 实现）。
 */
public class Comment extends ObservableEntity {
    private final String commentId;
    private final String postId;
    private final String authorId;
    private final String parentId; // null 表示顶层评论
    private final String content;
    private final LocalDateTime time;
    private final List<Comment> replies = new ArrayList<>();

    public Comment(String postId, String authorId, String parentId, String content) {
        this.commentId = UUID.randomUUID().toString();
        this.postId = postId;
        this.authorId = authorId;
        this.parentId = parentId;
        this.content = content;
        this.time = LocalDateTime.now();
    }

    public String getCommentId() { return commentId; }
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getParentId() { return parentId; }
    public String getContent() { return content; }
    public LocalDateTime getTime() { return time; }
    public List<Comment> getReplies() { return replies; }

    /**
     * 添加一条回复（楼中楼）。
     * 触发 REPLY_CREATED 事件，通知观察者（通常为被回复评论的作者）。
     */
    public void reply(User replier, String replyContent) {
        Comment reply = new Comment(this.postId, replier.getStudentID(), this.commentId, replyContent);
        replies.add(reply);

        Event event = new Event(
                EventType.REPLY_CREATED,
                replier,
                String.format("%s 回复了你的评论：%s", replier.getNickname(), replyContent)
        );
        // 通知已订阅该评论的观察者
        notifyObservers(event);
    }

    /**
     * 便捷重载：仅提供内容，默认以当前登录用户作为回复者。
     */
    public void reply(String content) {
        com.yunhang.forum.model.session.UserSession session = com.yunhang.forum.model.session.UserSession.getInstance();
        User replier = session.getCurrentUser();
        if (replier == null) {
            // 无登录态则仅追加数据，不发通知
            replies.add(new Comment(this.postId, this.authorId, this.commentId, content));
            return;
        }
        reply(replier, content);
    }
}
