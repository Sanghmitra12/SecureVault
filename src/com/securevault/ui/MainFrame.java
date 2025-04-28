package com.securevault.ui;

import com.securevault.service.AuthenticationService;
import com.securevault.service.VaultService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame that contains all UI panels
 */
public class MainFrame extends JFrame {
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private VaultPanel vaultPanel;
    private final AuthenticationService authService;
    private final VaultService vaultService;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    // Constants for panel names
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String REGISTER_PANEL = "REGISTER";
    public static final String VAULT_PANEL = "VAULT";

    public MainFrame() {
        // Set up the frame
        setTitle("SecureVault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        // Initialize services
        authService = new AuthenticationService();
        vaultService = new VaultService();

        // Set up the card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize panels
        initializePanels();

        // Add panels to the content panel
        contentPanel.add(loginPanel, LOGIN_PANEL);
        contentPanel.add(registerPanel, REGISTER_PANEL);
        contentPanel.add(vaultPanel, VAULT_PANEL);

        // Add content panel to the frame
        add(contentPanel);

        // Show the login panel by default
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }

    /**
     * Initialize UI panels
     */
    private void initializePanels() {
        // Initialize the login panel
        loginPanel = new LoginPanel(authService, event -> {
            // Handle login success
            cardLayout.show(contentPanel, VAULT_PANEL);
            vaultPanel.refreshVaultItems();
        }, event -> {
            // Switch to register panel
            cardLayout.show(contentPanel, REGISTER_PANEL);
        });

        // Initialize the register panel
        registerPanel = new RegisterPanel(authService, event -> {
            // Handle registration success
            cardLayout.show(contentPanel, LOGIN_PANEL);
            JOptionPane.showMessageDialog(this,
                "Registration successful! Please log in.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE);
        }, event -> {
            // Switch back to login panel
            cardLayout.show(contentPanel, LOGIN_PANEL);
        });

        // Initialize the vault panel
        vaultPanel = new VaultPanel(vaultService, event -> {
            // Handle logout
            cardLayout.show(contentPanel, LOGIN_PANEL);
        });
    }
}
