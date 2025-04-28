package com.securevault.ui;

import com.securevault.model.PasswordEntry;
import com.securevault.model.VaultItem;
import com.securevault.service.VaultService;
import com.securevault.exceptions.VaultException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for displaying and managing vault items
 */
public class VaultPanel extends JPanel {
    private final VaultService vaultService;
    private final ActionListener logoutListener;
    private final JTable vaultTable;
    private final DefaultTableModel tableModel;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;
    private final JButton logoutButton;
    private List<VaultItem> currentItems;

    public VaultPanel(VaultService vaultService, ActionListener logoutListener) {
        this.vaultService = vaultService;
        this.logoutListener = logoutListener;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("SecureVault - Vault");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));
        logoutButton = new JButton("Logout");
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Service", "Username", "Password"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        vaultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vaultTable);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Entry");
        editButton = new JButton("Edit Entry");
        deleteButton = new JButton("Delete Entry");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setupActionListeners();
        refreshVaultItems();
    }

    private void setupActionListeners() {
        logoutButton.addActionListener(e -> {
            if (logoutListener != null) {
                logoutListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "LOGOUT"));
            }
        });

        addButton.addActionListener(e -> {
            JTextField serviceField = new JTextField();
            JTextField userField = new JTextField();
            JTextField passField = new JTextField();

            Object[] fields = {
                    "Service Name:", serviceField,
                    "Username:", userField,
                    "Password:", passField
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "Add New Entry", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    PasswordEntry entry = new PasswordEntry(0, serviceField.getText(), userField.getText(), passField.getText());
                    vaultService.addEntry(null, entry);
                    refreshVaultItems();
                    JOptionPane.showMessageDialog(this, "Entry added successfully!");
                } catch (VaultException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding entry: " + ex.getMessage());
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = vaultTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                String currService = (String) tableModel.getValueAt(row, 1);
                String currUser = (String) tableModel.getValueAt(row, 2);
                String currPass = (String) tableModel.getValueAt(row, 3);

                JTextField serviceField = new JTextField(currService);
                JTextField userField = new JTextField(currUser);
                JTextField passField = new JTextField(currPass);

                Object[] fields = {
                        "Service Name:", serviceField,
                        "Username:", userField,
                        "Password:", passField
                };
                int res = JOptionPane.showConfirmDialog(this, fields, "Edit Entry", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    try {
                        PasswordEntry updated = new PasswordEntry(id, serviceField.getText(), userField.getText(), passField.getText());
                        vaultService.updateEntry(null, updated);
                        refreshVaultItems();
                        JOptionPane.showMessageDialog(this, "Entry updated successfully!");
                    } catch (VaultException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating entry: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an entry to edit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int row = vaultTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        vaultService.deleteEntry(null, id);
                        refreshVaultItems();
                        JOptionPane.showMessageDialog(this, "Entry deleted successfully!");
                    } catch (VaultException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting entry: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an entry to delete.");
            }
        });
    }

    public void refreshVaultItems() {
        try {
            currentItems = vaultService.getEntries(null);
            updateTable();
        } catch (VaultException e) {
            JOptionPane.showMessageDialog(this, "Error loading vault: " + e.getMessage());
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (VaultItem vi : currentItems) {
            PasswordEntry pe = (PasswordEntry) vi;
            tableModel.addRow(new Object[]{pe.getId(), pe.getServiceName(), pe.getUsername(), pe.getPassword()});
        }
    }
}
