package com.yunhang.forum.controller.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField studentField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onLogin(ActionEvent e) {
        String studentId = studentField != null ? studentField.getText() : "";
        String password = passwordField != null ? passwordField.getText() : "";
        // TODO: authenticate and navigate; for now, print
        System.out.println("Login clicked: " + studentId + "/" + (password.isEmpty() ? "(empty)" : "****"));
    }
}

