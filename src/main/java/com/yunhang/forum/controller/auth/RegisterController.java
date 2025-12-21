package com.yunhang.forum.controller.auth;

import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ResourcePaths;
import com.yunhang.forum.util.TaskRunner;
import com.yunhang.forum.util.ViewManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class RegisterController {

    @FXML private TextField studentIdField;
    @FXML private TextField nicknameField;
    @FXML private TextField emailPrefixField;
    @FXML private TextField verificationCodeField;
    @FXML private Button sendCodeButton;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();
    private Timeline countdownTimeline;
    private int secondsRemaining = 60;
    private static final String REQUIRED_SUFFIX = "@buaa.edu.cn";

    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error", "success");
        messageLabel.getStyleClass().add(success ? "success" : "error");
    }

    @FXML
    protected void initialize() {
        // 初始化
    }

    @FXML
    protected void handleSendCodeAction() {
        String emailPrefix = emailPrefixField.getText().trim();

        if (emailPrefix.isEmpty()) {
            setMessage("邮箱前缀不能为空", false);
            return;
        }

        String email = emailPrefix + REQUIRED_SUFFIX;

        // 网络 I/O：放到后台线程，避免阻塞 UI
        sendCodeButton.setDisable(true);
        sendCodeButton.setText("发送中...");
        setMessage("正在发送验证码...", true);

        Task<Boolean> sendTask = new Task<>() {
            @Override
            protected Boolean call() {
                return userService.sendVerificationCode(email);
            }
        };

        sendTask.setOnSucceeded(event -> {
            Boolean ok = sendTask.getValue();
            if (Boolean.TRUE.equals(ok)) {
                setMessage("验证码已发送到 " + email + "，请查收", true);
                startCountdown();
            } else {
                if (!UserService.isSmtpConfigured()) {
                    setMessage(UserService.smtpConfigHelp(), false);
                } else {
                    setMessage("验证码发送失败，请稍后再试", false);
                }
                sendCodeButton.setDisable(false);
                sendCodeButton.setText("发送验证码");
            }
        });

        sendTask.setOnFailed(event -> {
            setMessage("验证码发送失败，请稍后再试", false);
            sendCodeButton.setDisable(false);
            sendCodeButton.setText("发送验证码");
        });

        TaskRunner.runAsync(sendTask);
    }

    private void startCountdown() {
        secondsRemaining = 60;
        sendCodeButton.setDisable(true);
        sendCodeButton.setText("重新发送 (" + secondsRemaining + ")");

        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsRemaining--;
                    if (secondsRemaining > 0) {
                        sendCodeButton.setText("重新发送 (" + secondsRemaining + ")");
                    } else {
                        countdownTimeline.stop();
                        sendCodeButton.setDisable(false);
                        sendCodeButton.setText("发送验证码");
                    }
                })
        );
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    @FXML
    protected void handleRegisterButtonAction() {
        String studentId = studentIdField.getText();
        String nickname = nicknameField.getText();
        String emailPrefix = emailPrefixField.getText().trim();
        String verificationCode = verificationCodeField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 拼接完整的邮箱
        String email = emailPrefix + REQUIRED_SUFFIX;

        // 1. 客户端输入校验
        if (studentId.isEmpty() || nickname.isEmpty() || emailPrefix.isEmpty() || verificationCode.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setMessage("所有字段都不能为空", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            setMessage("两次输入的密码不一致", false);
            return;
        }

        // 2. 验证码校验
        if (!userService.isVerificationCodeValid(email, verificationCode)) {
            setMessage("验证码错误或已过期", false);
            return;
        }

        // 3. 学号唯一性校验
        if (userService.isStudentIdExists(studentId)) {
            setMessage("该学号已被注册", false);
            return;
        }

        // 4. 调用业务逻辑
        if (userService.registerStudent(studentId, nickname, password)) {
            setMessage("注册成功，正在跳转到登录页...", true);
            stopCountdown();
            handleBackToLoginAction();
        } else {
            setMessage("注册失败，请检查输入", false);
        }
    }

    @FXML
    protected void handleBackToLoginAction() {
        stopCountdown();
        ViewManager.switchScene(ResourcePaths.FXML_AUTH_LOGIN);
    }
}
