package com.securevault.ui;

import com.securevault.service.AuthenticationService;
import com.securevault.exceptions.AuthenticationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for user registration
 */
public class RegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JButton registerButton;
    private final JButton backButton;
    private final AuthenticationService authService;
    private final ActionListener registerSuccessListener;
    private final ActionListener backButtonListener;

    public RegisterPanel(AuthenticationService authService,
                         ActionListener registerSuccessListener,
                         ActionListener backButtonListener) {
        this.authService = authService;
        this.registerSuccessListener = registerSuccessListener;
        this.backButtonListener = backButtonListener;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("SecureVault Registration", JLabel.CENTER);
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

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setupActionListeners();
    }

    private void setupActionListeners() {
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Register the user
                authService.signup(username, password);

                usernameField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");

                if (registerSuccessListener != null) {
                    registerSuccessListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "REGISTER_SUCCESS"));
                }

            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this,
                        "Registration failed: " + ex.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            if (backButtonListener != null) {
                backButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "BACK_BUTTON"));
            }
        });
    }
}
