package com.yunhang.forum.model;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.PostImage;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Post类单元测试
 * 验证帖子核心功能：创建、发布、状态管理、热度计算等
 */
public class PostTest {

  public static void main(String[] args) {
    System.out.println("=".repeat(60));
    System.out.println("              POST 类单元测试");
    System.out.println("=".repeat(60));

    int testCount = 0;
    int passedCount = 0;

    try {
      // 测试1：基础构造功能
      System.out.println("\n📝 测试1: 基础构造功能");
      testCount++;
      Post post1 = new Post("Java学习心得",
          "今天学习了Java集合框架，收获很大！\n" +
              "特别是HashMap的工作原理。",
          "user_001",
          PostCategory.LEARNING);

      System.out.println("✅ 帖子创建成功");
      System.out.println("   帖子ID: " + post1.getPostId());
      System.out.println("   标题: " + post1.getTitle());
      System.out.println("   作者: " + post1.getDisplayAuthor());
      System.out.println("   分类: " + post1.getCategoryDisplay());
      System.out.println("   初始状态: " + post1.getStatus());

      if (post1.getStatus() == PostStatus.DRAFT) {
        System.out.println("✓ 初始状态为草稿，测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 初始状态不正确");
      }

      // 测试2：发布功能
      System.out.println("\n📝 测试2: 发布功能");
      testCount++;
      boolean publishResult = post1.publish();
      System.out.println("   发布结果: " + publishResult);
      System.out.println("   发布后状态: " + post1.getStatus());
      System.out.println("   发布时间: " + post1.getFormattedPublishTime());

      if (publishResult && post1.getStatus() == PostStatus.PUBLISHED) {
        System.out.println("✓ 发布功能测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 发布功能测试失败");
      }

      // 测试3：匿名发布
      System.out.println("\n📝 测试3: 匿名发布");
      testCount++;
      Post anonymousPost = new Post("匿名分享", "这是一个匿名帖子", "user_secret", PostCategory.CAMPUS_LIFE);
      anonymousPost.publishAnonymously();
      System.out.println("   匿名状态: " + anonymousPost.isAnonymous());
      System.out.println("   显示作者: " + anonymousPost.getDisplayAuthor());
      System.out.println("   真实作者: " + anonymousPost.getAuthorId());

      if (anonymousPost.isAnonymous() && "某同学".equals(anonymousPost.getDisplayAuthor())) {
        System.out.println("✓ 匿名发布测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 匿名发布测试失败");
      }

      // 测试4：热度计算
      System.out.println("\n📝 测试4: 热度计算");
      testCount++;
      Post hotPost = new Post("热门话题", "这是一个热门帖子", "user_hot", PostCategory.ACTIVITY);
      hotPost.publish();

      // 模拟用户互动
      for (int i = 0; i < 100; i++) hotPost.incrementViewCount();  // 100次浏览
      for (int i = 0; i < 50; i++) hotPost.incrementLikeCount();    // 50次点赞
      for (int i = 0; i < 30; i++) hotPost.incrementCommentCount(); // 30条评论

      double hotScore = hotPost.calculateHotScore();
      double expectedScore = 100 * 0.3 + 50 * 0.4 + 30 * 0.3; // 30 + 20 + 9 = 59
      System.out.println("   浏览量: " + hotPost.getViewCount());
      System.out.println("   点赞数: " + hotPost.getLikeCount());
      System.out.println("   评论数: " + hotPost.getCommentCount());
      System.out.println("   计算热度: " + String.format("%.2f", hotScore));
      System.out.println("   预期热度: " + expectedScore);

      if (Math.abs(hotScore - expectedScore) < 0.01) {
        System.out.println("✓ 热度计算测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 热度计算测试失败");
      }

      // 测试5：软删除与恢复
      System.out.println("\n📝 测试5: 软删除与恢复");
      testCount++;
      Post deleteTestPost = new Post("待删除帖子", "测试删除功能", "user_test", PostCategory.SECOND_HAND);
      deleteTestPost.publish();

      System.out.println("   删除前状态: " + deleteTestPost.getStatus());
      deleteTestPost.softDelete();
      System.out.println("   删除后状态: " + deleteTestPost.getStatus());

      boolean restoreResult = deleteTestPost.restore();
      System.out.println("   恢复结果: " + restoreResult);
      System.out.println("   恢复后状态: " + deleteTestPost.getStatus());

      if (deleteTestPost.getStatus() == PostStatus.DELETED && restoreResult &&
          deleteTestPost.getStatus() == PostStatus.PUBLISHED) {
        System.out.println("✓ 软删除与恢复测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 软删除与恢复测试失败");
      }

      // 测试6：强制删除
      System.out.println("\n📝 测试6: 强制删除");
      testCount++;
      Post forceDeletePost = new Post("强制删除测试", "管理员删除测试", "user_normal", PostCategory.QNA);
      forceDeletePost.publish();

      boolean forceDeleteResult = forceDeletePost.forceDelete();
      System.out.println("   强制删除结果: " + forceDeleteResult);
      System.out.println("   删除后状态: " + forceDeletePost.getStatus());
      System.out.println("   是否强制删除标记: " + forceDeletePost.isForceDeleted());

      // 尝试恢复强制删除的帖子
      boolean restoreAfterForce = forceDeletePost.restore();
      System.out.println("   恢复尝试结果: " + restoreAfterForce);

      if (forceDeleteResult && forceDeletePost.isForceDeleted() && !restoreAfterForce) {
        System.out.println("✓ 强制删除测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 强制删除测试失败");
      }

      // 测试7：图片管理
      System.out.println("\n📝 测试7: 图片管理");
      testCount++;
      Post imagePost = new Post("带图片的帖子", "这是一个包含图片的帖子", "user_photo", PostCategory.CAMPUS_LIFE);

      PostImage image1 = new PostImage("uploads/posts/2024/12", "java_code.png");
      image1.setOriginalName("my_java_code.png");
      image1.setFileSize(1024 * 1024); // 1MB

      PostImage image2 = new PostImage("uploads/posts/2024/12", "result.jpg");
      image2.setOriginalName("test_result.jpg");
      image2.setFileSize(512 * 1024); // 512KB

      imagePost.addImage(image1);
      imagePost.addImage(image2);

      System.out.println("   图片数量: " + imagePost.getImageCount());
      System.out.println("   是否有图片: " + imagePost.hasImages());
      System.out.println("   图片1信息: " + image1);
      System.out.println("   图片1类型支持: " + image1.isSupportedFileType());
      System.out.println("   图片1Web路径: " + image1.getWebPath());

      // 测试图片查找
      PostImage foundImage = imagePost.findImageByOriginalName("my_java_code.png");
      System.out.println("   查找图片结果: " + (foundImage != null ? "找到" : "未找到"));

      // 测试图片移除
      boolean removed = imagePost.removeImage(image1);
      System.out.println("   移除图片结果: " + removed);
      System.out.println("   移除后图片数量: " + imagePost.getImageCount());

      if (imagePost.getImageCount() == 1 && removed) {
        System.out.println("✓ 图片管理测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 图片管理测试失败");
      }

      // 测试8：字段验证
      System.out.println("\n📝 测试8: 字段验证");
      testCount++;
      Post validatePost = new Post("验证测试", "这是一个验证测试", "user_validate", PostCategory.EMPLOYMENT);

      System.out.println("   标题验证: " + validatePost.validateTitle());
      System.out.println("   内容验证: " + validatePost.validateContent());
      System.out.println("   分类验证: " + validatePost.validateCategory());
      System.out.println("   发布条件验证: " + validatePost.isPublishable());

      // 测试无效数据
      Post invalidPost = new Post("", "", "user_invalid", null);
      System.out.println("   空标题验证: " + invalidPost.validateTitle());
      System.out.println("   空内容验证: " + invalidPost.validateContent());
      System.out.println("   空分类验证: " + invalidPost.validateCategory());

      if (validatePost.validateTitle() && validatePost.validateContent() &&
          validatePost.validateCategory() && !invalidPost.validateTitle()) {
        System.out.println("✓ 字段验证测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 字段验证测试失败");
      }

      // 测试9：权限验证
      System.out.println("\n📝 测试9: 权限验证");
      testCount++;
      Post permissionPost = new Post("权限测试", "权限验证", "user_owner", PostCategory.LEARNING);
      permissionPost.publish();

      System.out.println("   作者编辑权限: " + permissionPost.canBeEditedBy("user_owner"));
      System.out.println("   他人编辑权限: " + permissionPost.canBeEditedBy("user_other"));
      System.out.println("   是否允许评论: " + permissionPost.isCommentable());
      System.out.println("   真实作者(管理员): " + permissionPost.getRealAuthorForAdmin("admin_001"));
      System.out.println("   真实作者(普通用户): " + permissionPost.getRealAuthorForAdmin("user_normal"));

      if (permissionPost.canBeEditedBy("user_owner") &&
          !permissionPost.canBeEditedBy("user_other") &&
          permissionPost.isCommentable()) {
        System.out.println("✓ 权限验证测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 权限验证测试失败");
      }

      // 测试10：数据转换
      System.out.println("\n📝 测试10: 数据转换");
      testCount++;
      Post convertPost = new Post("数据转换测试", "测试toListViewModel方法", "user_convert", PostCategory.ANNOUNCEMENT);
      convertPost.publish();

      Map<String, Object> listView = convertPost.toListViewModel();
      Map<String, Object> detailView = convertPost.toDetailViewModel();

      System.out.println("   列表视图键数量: " + listView.size());
      System.out.println("   详情视图键数量: " + detailView.size());
      System.out.println("   包含标题: " + listView.containsKey("title"));
      System.out.println("   包含热度: " + listView.containsKey("hotScore"));
      System.out.println("   详情包含完整内容: " + detailView.containsKey("fullContent"));

      if (listView.size() >= 10 && detailView.size() > listView.size() &&
          listView.containsKey("title") && listView.containsKey("hotScore")) {
        System.out.println("✓ 数据转换测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 数据转换测试失败");
      }

      // 测试11：排序比较器
      System.out.println("\n📝 测试11: 排序比较器");
      testCount++;

      Post postA = new Post("帖子A", "内容A", "user_a", PostCategory.LEARNING);
      Post postB = new Post("帖子B", "内容B", "user_b", PostCategory.CAMPUS_LIFE);

      // 设置不同的互动数据
      postA.setViewCount(100);
      postA.setLikeCount(20);
      postA.setCommentCount(10);

      postB.setViewCount(200);
      postB.setLikeCount(40);
      postB.setCommentCount(20);

      // 测试热度排序
      Comparator<Post> hotComparator = Post.getHotnessComparator();
      int hotCompare = hotComparator.compare(postA, postB);
      System.out.println("   帖子A热度: " + String.format("%.2f", postA.calculateHotScore()));
      System.out.println("   帖子B热度: " + String.format("%.2f", postB.calculateHotScore()));
      System.out.println("   热度比较结果: " + (hotCompare > 0 ? "A更热" : hotCompare < 0 ? "B更热" : "一样热"));

      // 测试时间排序
      Comparator<Post> timeComparator = Post.getTimeComparator();
      int timeCompare = timeComparator.compare(postA, postB);
      System.out.println("   时间比较结果: " + (timeCompare > 0 ? "A更新" : timeCompare < 0 ? "B更新" : "同时"));

      if (hotCompare < 0 && Math.abs(timeCompare) <= 1) { // B更热，时间相近
        System.out.println("✓ 排序比较器测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 排序比较器测试失败");
      }

      // 测试12：状态转换验证
      System.out.println("\n📝 测试12: 状态转换");
      testCount++;

      Post statusPost = new Post("状态测试", "测试状态转换", "user_status", PostCategory.LEARNING);

      System.out.println("   草稿→发布: " + statusPost.canTransitionTo(PostStatus.PUBLISHED));
      System.out.println("   草稿→删除: " + statusPost.canTransitionTo(PostStatus.DELETED));
      System.out.println("   草稿→锁定: " + statusPost.canTransitionTo(PostStatus.LOCKED));

      statusPost.publish();
      System.out.println("   发布后状态: " + statusPost.getStatus());
      System.out.println("   发布→锁定: " + statusPost.canTransitionTo(PostStatus.LOCKED));
      System.out.println("   发布→归档: " + statusPost.canTransitionTo(PostStatus.ARCHIVED));

      boolean lockResult = statusPost.safeTransitionTo(PostStatus.LOCKED);
      System.out.println("   锁定结果: " + lockResult);
      System.out.println("   锁定后状态: " + statusPost.getStatus());

      if (statusPost.getStatus() == PostStatus.LOCKED && lockResult) {
        System.out.println("✓ 状态转换测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 状态转换测试失败");
      }

      // 测试13：工具方法
      System.out.println("\n📝 测试13: 工具方法");
      testCount++;

      Post toolPost = new Post("工具方法测试", "测试各种工具方法", "user_tool", PostCategory.LEARNING);
      toolPost.publish();

      System.out.println("   是否可见: " + toolPost.isVisible());
      System.out.println("   是否可编辑: " + toolPost.isEditable());
      System.out.println("   是否热门(热度>10): " + toolPost.isHot());
      System.out.println("   是否今日发布: " + toolPost.isToday());
      System.out.println("   简短标识: " + toolPost.getShortIdentifier());

      // 设置一些互动数据
      toolPost.setViewCount(50);
      toolPost.setLikeCount(20);
      toolPost.setCommentCount(10);
      System.out.println("   设置互动后是否热门: " + toolPost.isHot());

      boolean allToolsWork = toolPost.isVisible() &&
          toolPost.isEditable() &&
          !toolPost.isHot() &&  // 初始热度为0，不是热门
          toolPost.getShortIdentifier().length() > 0;

      if (allToolsWork) {
        System.out.println("✓ 工具方法测试通过");
        passedCount++;
      } else {
        System.out.println("✗ 工具方法测试失败");
      }

      // 测试14：toString方法
      System.out.println("\n📝 测试14: toString方法");
      testCount++;

      Post stringPost = new Post("测试帖子", "测试toString", "user_string", PostCategory.LEARNING);
      stringPost.publish();
      stringPost.setViewCount(100);
      stringPost.setLikeCount(50);
      stringPost.setCommentCount(20);

      String postString = stringPost.toString();
      System.out.println("   toString结果: " + postString);
      System.out.println("   包含ID: " + postString.contains("postId"));
      System.out.println("   包含标题: " + postString.contains("测试帖子"));
      System.out.println("   包含热度: " + postString.contains("hotScore"));

      if (postString.contains("测试帖子") && postString.contains("hotScore")) {
        System.out.println("✓ toString测试通过");
        passedCount++;
      } else {
        System.out.println("✗ toString测试失败");
      }

      // 测试15：equals和hashCode
      System.out.println("\n📝 测试15: equals和hashCode");
      testCount++;

      Post samePost1 = new Post("相同帖子", "内容相同", "user_same", PostCategory.LEARNING);
      Post samePost2 = new Post("不同帖子", "内容不同", "user_different", PostCategory.CAMPUS_LIFE);

      // 设置相同的ID
      String testId = "test_id_123";
      samePost1.setPostId(testId);
      samePost2.setPostId(testId);

      System.out.println("   相同ID比较: " + samePost1.equals(samePost2));
      System.out.println("   hashCode比较: " + (samePost1.hashCode() == samePost2.hashCode()));

      samePost2.setPostId("different_id");
      System.out.println("   不同ID比较: " + samePost1.equals(samePost2));

      if (samePost1.equals(samePost2) &&
          samePost1.hashCode() == samePost2.hashCode() &&
          !samePost1.equals(new Object())) {
        System.out.println("✓ equals和hashCode测试通过");
        passedCount++;
      } else {
        System.out.println("✗ equals和hashCode测试失败");
      }

    } catch (Exception e) {
      System.out.println("\n❌ 测试过程中出现异常: " + e.getMessage());
      e.printStackTrace();
    }

    // 测试结果总结
    System.out.println("\n" + "=".repeat(60));
    System.out.println("                   测试结果总结");
    System.out.println("=".repeat(60));
    System.out.println("总测试数: " + testCount);
    System.out.println("通过数: " + passedCount);
    System.out.println("失败数: " + (testCount - passedCount));
    System.out.println("通过率: " + String.format("%.1f", (passedCount * 100.0 / testCount)) + "%");

    if (passedCount == testCount) {
      System.out.println("\n🎉 所有测试通过！Post类实现正确。");
    } else {
      System.out.println("\n⚠️  部分测试失败，请检查实现。");
    }
    System.out.println("=".repeat(60));

    // 显示一个示例帖子信息
    System.out.println("\n📋 示例帖子信息:");
    System.out.println("=".repeat(60));
    Post examplePost = new Post(
        "Java多线程编程实践分享",
        "最近在项目中应用了Java多线程技术，分享一下经验：\n" +
            "1. 使用线程池管理线程资源\n" +
            "2. 合理使用锁机制避免竞争\n" +
            "3. 注意线程安全的数据结构选择",
        "student_20241234",
        PostCategory.LEARNING
    );

    examplePost.publish();
    examplePost.incrementViewCount();
    examplePost.incrementViewCount();
    examplePost.incrementLikeCount();
    examplePost.incrementCommentCount();

    System.out.println("帖子ID: " + examplePost.getPostId());
    System.out.println("标题: " + examplePost.getTitle());
    System.out.println("作者: " + examplePost.getDisplayAuthor());
    System.out.println("分类: " + examplePost.getCategoryDisplay());
    System.out.println("状态: " + examplePost.getStatus().getStatusName());
    System.out.println("发布时间: " + examplePost.getFormattedPublishTime());
    System.out.println("浏览量: " + examplePost.getViewCount());
    System.out.println("点赞数: " + examplePost.getLikeCount());
    System.out.println("评论数: " + examplePost.getCommentCount());
    System.out.println("热度得分: " + String.format("%.2f", examplePost.calculateHotScore()));
    System.out.println("是否可见: " + examplePost.isVisible());
    System.out.println("是否可评论: " + examplePost.isCommentable());
    System.out.println("是否为今日发布: " + examplePost.isToday());

    // 测试内容方法
    System.out.println("\n内容摘要(30字): " + examplePost.getContentSummary(30));
    List<String> lines = examplePost.getContentLines();
    System.out.println("内容行数: " + lines.size());
    System.out.println("第一行内容: " + (lines.isEmpty() ? "" : lines.get(0)));

    System.out.println("=".repeat(60));
    System.out.println("✅ PostTest 执行完成");
  }
}
