package com.securevault.ui;

import com.securevault.service.AuthenticationService;
import com.securevault.exceptions.AuthenticationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for user login
 */
public class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final AuthenticationService authService;
    private final ActionListener loginSuccessListener;
    private final ActionListener registerButtonListener;

    public LoginPanel(AuthenticationService authService,
                      ActionListener loginSuccessListener,
                      ActionListener registerButtonListener) {
        this.authService = authService;
        this.loginSuccessListener = loginSuccessListener;
        this.registerButtonListener = registerButtonListener;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("SecureVault Login", JLabel.CENTER);
        headerLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setupActionListeners();
    }

    private void setupActionListeners() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Use AuthenticationService to login
                authService.login(username, password);

                usernameField.setText("");
                passwordField.setText("");

                if (loginSuccessListener != null) {
                    loginSuccessListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "LOGIN_SUCCESS"));
                }

            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this,
                        "Login failed: " + ex.getMessage(),
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            if (registerButtonListener != null) {
                registerButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "REGISTER_BUTTON"));
            }
        });
    }
}
