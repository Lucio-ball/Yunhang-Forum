package com.yunhang.forum.controller.post;

import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PostDetailController: 负责渲染帖子详情与评论列表。
 */
public class PostDetailController {
    private Post currentPost;

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label timeLabel;
    @FXML private Label contentLabel;
    @FXML private VBox commentsContainer;
    @FXML private TextField commentInput;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 初始化数据：由列表页或路由传递进来
    public void initData(Post post) {
        this.currentPost = post;
        if (post == null) return;
        titleLabel.setText(post.getTitle());
        authorLabel.setText(post.getDisplayAuthor());
        timeLabel.setText(post.getFormattedPublishTime());
        contentLabel.setText(post.getFullContent());
        renderComments();
    }

    private void renderComments() {
        commentsContainer.getChildren().clear();
        if (currentPost == null) return;
        List<Comment> comments = currentPost.getComments();
        for (Comment c : comments) {
            VBox box = buildCommentNode(c);
            commentsContainer.getChildren().add(box);
        }
    }

    private VBox buildCommentNode(Comment c) {
        Label author = new Label("@" + c.getAuthorId());
        author.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");
        Label content = new Label(c.getContent());
        content.setWrapText(true);
        Label time = new Label(c.getTime().format(TIME_FMT));
        time.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        VBox container = new VBox(2);
        container.getChildren().addAll(author, content, time);
        container.setStyle("-fx-background-color: #f7f9fc; -fx-padding: 8; -fx-background-radius: 6;");
        return container;
    }

    @FXML
    private void onSendComment() {
        if (currentPost == null) return;
        String text = commentInput.getText();
        if (text == null || text.trim().isEmpty()) {
            commentInput.setStyle("-fx-border-color: #ff4d4f;");
            return;
        }
        commentInput.setStyle("");

        // 获取当前用户作为评论作者
        User currentUser = UserSession.getInstance().getCurrentUser();
        String authorId = currentUser != null ? currentUser.getUserID() : "anonymous";
        Comment newComment = new Comment(currentPost.getPostId(), authorId, null, text.trim());

        // 调用服务层添加评论（简单示例：直接更新模型并刷新UI）
        boolean ok = PostService.getInstance().addComment(currentPost, newComment);
        if (ok) {
            commentInput.clear();
            // 动态添加到 UI 末尾
            commentsContainer.getChildren().add(buildCommentNode(newComment));
        } else {
            // 失败提示样式（可换成对话框）
            commentInput.setStyle("-fx-border-color: #ff4d4f;");
        }
    }
}
