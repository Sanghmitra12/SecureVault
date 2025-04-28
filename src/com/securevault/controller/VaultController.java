package com.securevault.controller;

import com.securevault.model.*;
import com.securevault.service.VaultService;
import com.securevault.exceptions.VaultException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main password vault dashboard. Also implements auto-logout.
 */
public class VaultController {
    private User user;
    private VaultService vaultService = new VaultService();
    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable table;
    private Timer autoLogoutTimer;
    private long lastActivityTime = System.currentTimeMillis();

    public VaultController(User user) {
        this.user = user;
        createUI();
        resetAutoLogout();
    }

    private void createUI() {
        frame = new JFrame("SecureVault - Vault (" + user.getUsername() + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 460);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // North toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add New Entry");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnLogout = new JButton("Logout");
        JTextField tfSearch = new JTextField(20);
        tfSearch.setToolTipText("Search service...");
        toolbar.add(btnAdd); toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(tfSearch);
        toolbar.add(btnLogout);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Service", "Username", "Password"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        refreshTable("");

        // --- Event handlers
        ActionListener activityListener = e -> resetAutoLogout();

        btnAdd.addActionListener(e -> { resetAutoLogout(); showAddEntryDialog(); });
        btnEdit.addActionListener(e -> { resetAutoLogout(); showEditEntryDialog(); });
        btnDelete.addActionListener(e -> { resetAutoLogout(); deleteSelectedEntry(); });
        btnLogout.addActionListener(e -> { frame.dispose(); stopAutoLogout(); new LoginController(); });

        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                resetAutoLogout();
                refreshTable(tfSearch.getText().trim());
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        // Any mouse/key event on UI resets auto-logout
        frame.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { resetAutoLogout(); }});
        table.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { resetAutoLogout(); }});
        frame.addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) { resetAutoLogout(); }});

        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // --- Vault operations

    private void refreshTable(String filter) {
        tableModel.setRowCount(0);
        try {
            List<VaultItem> entries = vaultService.getEntries(user);
            for (VaultItem vi : entries) {
                if (filter.isEmpty() ||
                    vi.getServiceName().toLowerCase().contains(filter.toLowerCase()) ||
                    vi.getUsername().toLowerCase().contains(filter.toLowerCase())) {
                    tableModel.addRow(new Object[]{vi.getId(), vi.getServiceName(), vi.getUsername(), vi.getPassword()});
                }
            }
        } catch (VaultException e) {
            JOptionPane.showMessageDialog(frame, "Could not load vault: " + e.getMessage());
        }
    }

    private void showAddEntryDialog() {
        JTextField tfService = new JTextField();
        JTextField tfUser = new JTextField();
        JTextField tfPass = new JTextField();

        Object[] fields = {
                "Service Name:", tfService,
                "Username:", tfUser,
                "Password:", tfPass
        };
        int res = JOptionPane.showConfirmDialog(frame, fields, "New Entry", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String svc = tfService.getText().trim();
            String u = tfUser.getText().trim();
            String p = tfPass.getText();
            if (svc.isEmpty() || u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required");
                return;
            }
            PasswordEntry entry = new PasswordEntry(0, svc, u, p);
            try {
                vaultService.addEntry(user, entry);
                JOptionPane.showMessageDialog(frame, "Entry added!");
                refreshTable("");
            } catch (VaultException e) {
                JOptionPane.showMessageDialog(frame, "Add failed: " + e.getMessage());
            }
        }
    }

    private void showEditEntryDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Select an entry to edit.");
            return;
        }
        int entryId = (int) tableModel.getValueAt(row, 0);
        String currService = (String) tableModel.getValueAt(row, 1);
        String currUser = (String) tableModel.getValueAt(row, 2);
        String currPass = (String) tableModel.getValueAt(row, 3);

        JTextField tfService = new JTextField(currService);
        JTextField tfUser = new JTextField(currUser);
        JTextField tfPass = new JTextField(currPass);

        Object[] fields = {
                "Service Name:", tfService,
                "Username:", tfUser,
                "Password:", tfPass
        };
        int res = JOptionPane.showConfirmDialog(frame, fields, "Edit Entry", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String svc = tfService.getText().trim();
            String u = tfUser.getText().trim();
            String p = tfPass.getText();
            if (svc.isEmpty() || u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required");
                return;
            }
            PasswordEntry entry = new PasswordEntry(entryId, svc, u, p);
            try {
                vaultService.updateEntry(user, entry);
                JOptionPane.showMessageDialog(frame, "Entry updated!");
                refreshTable("");
            } catch (VaultException e) {
                JOptionPane.showMessageDialog(frame, "Update failed: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedEntry() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Select an entry to delete.");
            return;
        }
        int entryId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this entry?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                vaultService.deleteEntry(user, entryId);
                refreshTable("");
            } catch (VaultException e) {
                JOptionPane.showMessageDialog(frame, "Delete failed: " + e.getMessage());
            }
        }
    }

    // --- Auto-logout timer implementation (multi-threaded)
    private void resetAutoLogout() {
        lastActivityTime = System.currentTimeMillis();
        if (autoLogoutTimer == null) {
            autoLogoutTimer = new Timer();
            autoLogoutTimer.schedule(new TimerTask() {
                public void run() {
                    if (System.currentTimeMillis() - lastActivityTime > 60000) {
                        SwingUtilities.invokeLater(() -> {
                            frame.dispose();
                            JOptionPane.showMessageDialog(null, "Logged out due to inactivity.");
                            stopAutoLogout();
                            new LoginController();
                        });
                        cancel();
                    }
                }
            }, 10000, 5000);
        }
    }
    private void stopAutoLogout() {
        if (autoLogoutTimer != null) {
            autoLogoutTimer.cancel();
            autoLogoutTimer = null;
        }
    }
}
