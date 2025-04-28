
package com.securevault.controller;

import com.securevault.service.AuthenticationService;
import com.securevault.model.User;
import com.securevault.exceptions.AuthenticationException;
import javax.swing.*;
import java.awt.*;

/**
 * Handles login/signup and launches the vault dashboard.
 */
public class LoginController {
    private AuthenticationService authService = new AuthenticationService();

    public LoginController() {
        showLogin();
    }

    private void showLogin() {
        JFrame frame = new JFrame("SecureVault - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        JLabel luser = new JLabel("Username:");
        luser.setBounds(40, 40, 80, 25);
        JTextField tfUser = new JTextField();
        tfUser.setBounds(130, 40, 150, 25);
        JLabel lpass = new JLabel("Password:");
        lpass.setBounds(40, 75, 80, 25);
        JPasswordField tfPass = new JPasswordField();
        tfPass.setBounds(130, 75, 150, 25);
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 120, 100, 30);
        JButton btnSignup = new JButton("Sign up");
        btnSignup.setBounds(180, 120, 100, 30);
        JLabel status = new JLabel(" ");
        status.setBounds(40, 170, 250, 25);
        status.setForeground(Color.RED);
        frame.add(luser);
        frame.add(tfUser);
        frame.add(lpass);
        frame.add(tfPass);
        frame.add(btnLogin);
        frame.add(btnSignup);
        frame.add(status);
        btnLogin.addActionListener(e -> {
            String user = tfUser.getText().trim();
            String pass = new String(tfPass.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                status.setText("Enter username and password");
                return;
            }
            try {
                User loggedIn = authService.login(user, pass);
                frame.dispose();
                new VaultController(loggedIn);
            } catch (AuthenticationException ex) {
                status.setText(ex.getMessage());
            }
        });
        btnSignup.addActionListener(e -> {
            String user = tfUser.getText().trim();
            String pass = new String(tfPass.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                status.setText("Enter username and password");
                return;
            }
            try {
                authService.signup(user, pass);
                status.setForeground(new Color(34, 139, 34));
                status.setText("Signup success. Please login!");
            } catch (AuthenticationException ex) {
                status.setForeground(Color.RED);
                status.setText(ex.getMessage());
            }
        });
        frame.setVisible(true);
    }
}