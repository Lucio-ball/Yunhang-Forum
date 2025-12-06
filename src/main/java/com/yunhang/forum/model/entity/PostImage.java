package com.yunhang.forum.model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

/**
 * 帖子图片资源类 - 图片信息管理
 */
public class PostImage {
  private String imagePath;      // 相对路径，如 "uploads/posts/2024/12/"
  private String imageName;      // 存储的文件名，如 "post123_1638291234_789.png"
  private String originalName;   // 原始文件名，如 "my_photo.png"
  private LocalDateTime uploadTime;
  private long fileSize;         // 文件大小（字节）

  // 支持的图片扩展名
  private static final String[] SUPPORTED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};

  // 构造方法
  public PostImage() {
    this.uploadTime = LocalDateTime.now();
  }

  public PostImage(String imagePath, String imageName) {
    this.imagePath = imagePath;
    this.imageName = imageName;
    this.originalName = imageName;
    this.uploadTime = LocalDateTime.now();
  }

  // Getter和Setter
  public String getImagePath() { return imagePath; }
  public void setImagePath(String imagePath) { this.imagePath = imagePath; }

  public String getImageName() { return imageName; }
  public void setImageName(String imageName) { this.imageName = imageName; }

  public String getOriginalName() { return originalName; }
  public void setOriginalName(String originalName) { this.originalName = originalName; }

  public LocalDateTime getUploadTime() { return uploadTime; }
  public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }

  public long getFileSize() { return fileSize; }
  public void setFileSize(long fileSize) { this.fileSize = fileSize; }

  // ============ 核心业务方法 ============

  /**
   * 获取完整的相对路径（路径 + 文件名）
   */
  public String getRelativeFilePath() {
    if (imagePath == null || imageName == null) {
      return null;
    }
    // 确保路径格式正确
    String path = imagePath.endsWith("/") ? imagePath : imagePath + "/";
    return path + imageName;
  }

  /**
   * 获取完整的文件路径（基于项目根目录）
   */
  public String getFullFilePath() {
    String relativePath = getRelativeFilePath();
    if (relativePath == null) {
      return null;
    }
    // 假设图片存储在 data/uploads 目录下
    return "data/uploads/" + relativePath;
  }

  /**
   * 验证图片是否有效
   */
  public boolean isValid() {
    return imagePath != null && imageName != null &&
        !imagePath.trim().isEmpty() && !imageName.trim().isEmpty();
  }

  /**
   * 获取文件扩展名
   */
  public String getFileExtension() {
    if (imageName == null || !imageName.contains(".")) {
      return "";
    }
    return imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
  }

  /**
   * 获取原始文件扩展名
   */
  public String getOriginalExtension() {
    if (originalName == null || !originalName.contains(".")) {
      return "";
    }
    return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
  }

  /**
   * 验证是否支持该文件类型
   */
  public boolean isSupportedFileType() {
    String extension = getFileExtension();
    if (extension.isEmpty()) {
      return false;
    }
    for (String supportedExt : SUPPORTED_EXTENSIONS) {
      if (supportedExt.equals(extension)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 从文件创建图片对象（简化版）
   */
  public static PostImage createFromFile(File imageFile, String postId) {
    if (imageFile == null || !imageFile.exists()) {
      return null;
    }

    // 生成唯一文件名：帖子ID_时间戳_随机数.扩展名
    String originalName = imageFile.getName();
    String extension = getExtension(originalName);
    String timestamp = String.valueOf(System.currentTimeMillis());
    String random = String.valueOf((int)(Math.random() * 1000));
    String uniqueName = postId + "_" + timestamp + "_" + random + "." + extension;

    // 按年份/月份组织目录
    LocalDateTime now = LocalDateTime.now();
    String year = String.valueOf(now.getYear());
    String month = String.format("%02d", now.getMonthValue());
    String storagePath = "posts/" + year + "/" + month;

    PostImage postImage = new PostImage();
    postImage.setOriginalName(originalName);
    postImage.setImageName(uniqueName);
    postImage.setImagePath(storagePath);
    postImage.setFileSize(imageFile.length());

    return postImage;
  }

  /**
   * 生成缩略图文件名
   */
  public String getThumbnailName() {
    if (imageName == null || !imageName.contains(".")) {
      return imageName + "_thumb";
    }
    String nameWithoutExt = imageName.substring(0, imageName.lastIndexOf('.'));
    String extension = getFileExtension();
    return nameWithoutExt + "_thumb." + extension;
  }

  /**
   * 获取缩略图相对路径
   */
  public String getThumbnailPath() {
    String thumbnailName = getThumbnailName();
    if (thumbnailName == null || imagePath == null) {
      return null;
    }
    String path = imagePath.endsWith("/") ? imagePath : imagePath + "/";
    return path + thumbnailName;
  }

  /**
   * 获取友好的文件大小显示
   */
  public String getFormattedFileSize() {
    if (fileSize < 1024) {
      return fileSize + " B";
    } else if (fileSize < 1024 * 1024) {
      return String.format("%.1f KB", fileSize / 1024.0);
    } else {
      return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
  }

  /**
   * 获取格式化的上传时间
   */
  public String getFormattedUploadTime() {
    if (uploadTime == null) return "";
    return uploadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  /**
   * 获取用于Web显示的路径（假设有静态资源服务器）
   */
  public String getWebPath() {
    String relativePath = getRelativeFilePath();
    if (relativePath == null) {
      return "/images/default-post.png";
    }
    return "/uploads/" + relativePath;
  }

  /**
   * 获取缩略图的Web路径
   */
  public String getThumbnailWebPath() {
    String thumbnailPath = getThumbnailPath();
    if (thumbnailPath == null) {
      return "/images/default-thumb.png";
    }
    return "/uploads/" + thumbnailPath;
  }

  // 私有辅助方法
  private static String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "jpg"; // 默认扩展名
    }
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }

  @Override
  public String toString() {
    return "PostImage{" +
        "originalName='" + originalName + '\'' +
        ", imageName='" + imageName + '\'' +
        ", imagePath='" + imagePath + '\'' +
        ", uploadTime=" + getFormattedUploadTime() +
        ", fileSize=" + getFormattedFileSize() +
        ", webPath='" + getWebPath() + "'" +
        '}';
  }
}
