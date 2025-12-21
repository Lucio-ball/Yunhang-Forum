package com.yunhang.forum.service;

import com.yunhang.forum.util.AppConfig;
import com.yunhang.forum.util.LogUtil;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EmailService: SMTP sending + verification code cache.
 * Designed to be composed by UserService.
 */
public class EmailService {

  private static final long VERIFICATION_TTL_MILLIS = 5L * 60L * 1000L;

  private static final String ENV_SMTP_USER = "YH_FORUM_SMTP_USER";
  private static final String ENV_SMTP_PASSWORD = "YH_FORUM_SMTP_PASSWORD";
  private static final String ENV_SMTP_HOST = "YH_FORUM_SMTP_HOST";
  private static final String ENV_SMTP_PORT = "YH_FORUM_SMTP_PORT";

  private static final String PROP_SMTP_USER = "yunhang.smtp.user";
  private static final String PROP_SMTP_PASSWORD = "yunhang.smtp.password";
  private static final String PROP_SMTP_HOST = "yunhang.smtp.host";
  private static final String PROP_SMTP_PORT = "yunhang.smtp.port";

  private final ConcurrentHashMap<String, CodeEntry> cache = new ConcurrentHashMap<>();
  private final Random random = new Random();

  private record CodeEntry(String code, long expiresAtMillis) {
    boolean isExpired(long nowMillis) {
      return nowMillis >= expiresAtMillis;
    }
  }

  public static boolean isSmtpConfigured() {
    String user = getSmtpUser();
    String pwd = getSmtpPassword();
    return user != null && !user.isBlank() && pwd != null && !pwd.isBlank();
  }

  public static String smtpConfigHelp() {
    return "邮件服务未配置。请设置环境变量 "
        + ENV_SMTP_USER
        + "/"
        + ENV_SMTP_PASSWORD
        + "（推荐），或在 application.properties 设置 "
        + PROP_SMTP_USER
        + "/"
        + PROP_SMTP_PASSWORD
        + "。";
  }

  /**
   * Master diagram API: sendEmail(to, code)
   */
  public boolean sendEmail(String to, String code) {
    if (to == null || to.isBlank() || code == null || code.isBlank()) {
      return false;
    }

    String smtpUser = getSmtpUser();
    String smtpPassword = getSmtpPassword();
    String smtpHost = getSmtpHost();
    String smtpPort = getSmtpPort();

    if (smtpUser == null || smtpUser.isBlank() || smtpPassword == null || smtpPassword.isBlank()) {
      LogUtil.error("SMTP credentials not configured. Set env " + ENV_SMTP_USER + "/" + ENV_SMTP_PASSWORD
          + " or properties " + PROP_SMTP_USER + "/" + PROP_SMTP_PASSWORD, null);
      return false;
    }

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.ssl.enable", "true");

    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(smtpUser, smtpPassword);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUser));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject("【Yunhang-Forum】邮箱验证码");
      message.setText("您的注册验证码是：" + code + "，5分钟内有效。请勿泄露给他人。");

      Transport.send(message);
      LogUtil.info("邮件发送成功到: " + to);
      return true;
    } catch (MessagingException e) {
      LogUtil.error("邮件发送失败: " + e.getMessage(), e);
      return false;
    }
  }

  /**
   * Convenience API used by UserService: generate + cache + send.
   */
  public boolean sendVerificationCode(String email) {
    cleanupExpired();

    if (!isSmtpConfigured()) {
      return false;
    }

    String code = generateAndCache(email);
    boolean ok = sendEmail(email, code);
    if (!ok) {
      cache.remove(email);
    }
    return ok;
  }

  /**
   * Master diagram API: verifyCode(email, code)
   */
  public boolean verifyCode(String email, String code) {
    if (email == null || email.isBlank() || code == null || code.isBlank()) {
      return false;
    }

    CodeEntry entry = cache.get(email);
    if (entry == null) {
      return false;
    }

    long now = System.currentTimeMillis();
    if (entry.isExpired(now)) {
      cache.remove(email);
      return false;
    }

    if (entry.code().equals(code)) {
      cache.remove(email);
      return true;
    }

    return false;
  }

  private String generateAndCache(String email) {
    String code = String.format("%06d", random.nextInt(1_000_000));
    long expiresAt = System.currentTimeMillis() + VERIFICATION_TTL_MILLIS;
    cache.put(email, new CodeEntry(code, expiresAt));
    return code;
  }

  private void cleanupExpired() {
    long now = System.currentTimeMillis();
    cache.entrySet().removeIf(e -> e.getValue() == null || e.getValue().isExpired(now));
  }

  private static String getSmtpUser() {
    String env = System.getenv(ENV_SMTP_USER);
    if (env != null && !env.isBlank()) {
      return env;
    }
    return AppConfig.get(PROP_SMTP_USER);
  }

  private static String getSmtpPassword() {
    String env = System.getenv(ENV_SMTP_PASSWORD);
    if (env != null && !env.isBlank()) {
      return env;
    }
    return AppConfig.get(PROP_SMTP_PASSWORD);
  }

  private static String getSmtpHost() {
    String env = System.getenv(ENV_SMTP_HOST);
    if (env != null && !env.isBlank()) {
      return env;
    }
    String prop = AppConfig.get(PROP_SMTP_HOST);
    return (prop == null || prop.isBlank()) ? "smtp.163.com" : prop;
  }

  private static String getSmtpPort() {
    String env = System.getenv(ENV_SMTP_PORT);
    if (env != null && !env.isBlank()) {
      return env;
    }
    String prop = AppConfig.get(PROP_SMTP_PORT);
    return (prop == null || prop.isBlank()) ? "465" : prop;
  }
}
