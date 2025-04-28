package com.securevault.service;

import com.securevault.model.*;
import com.securevault.utils.DBConnection;
import com.securevault.utils.EncryptionUtil;
import com.securevault.exceptions.VaultException;

import java.sql.*;
import java.util.*;

public class VaultService {
    // Fetch user's vault entries
    public List<VaultItem> getEntries(User user) throws VaultException {
        List<VaultItem> list = new ArrayList<>();
        String q = "SELECT id, service_name, encrypted_username, encrypted_password FROM vault_entries WHERE user_id = ? ORDER BY service_name";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(q)) {
            pst.setInt(1, user.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry(
                        rs.getInt("id"),
                        rs.getString("service_name"),
                        rs.getString("encrypted_username"),
                        rs.getString("encrypted_password")
                );
                try {
                    entry.decrypt(EncryptionUtil.getEncryptionKey(user.getUsername()));
                } catch (Exception e) {
                    throw new VaultException("Decryption failed.");
                }
                list.add(entry);
            }
        } catch (SQLException e) {
            throw new VaultException("Could not fetch entries: " + e.getMessage());
        }
        return list;
    }

    // Add new entry
    public void addEntry(User user, PasswordEntry entry) throws VaultException {
        String sql = "INSERT INTO vault_entries (user_id, service_name, encrypted_username, encrypted_password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            PasswordEntry encrypted = new PasswordEntry(
                    0, entry.getServiceName(), entry.getUsername(), entry.getPassword());
            encrypted.encrypt(EncryptionUtil.getEncryptionKey(user.getUsername()));

            pst.setInt(1, user.getId());
            pst.setString(2, entry.getServiceName());
            pst.setString(3, encrypted.getUsername());
            pst.setString(4, encrypted.getPassword());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new VaultException("Add failed: " + e.getMessage());
        } catch (Exception e2) {
            throw new VaultException("Encryption error: " + e2.getMessage());
        }
    }

    public void deleteEntry(User user, int entryId) throws VaultException {
        String del = "DELETE FROM vault_entries WHERE id = ? AND user_id = ?";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(del)) {
            pst.setInt(1, entryId);
            pst.setInt(2, user.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new VaultException("Delete failed: " + e.getMessage());
        }
    }

    public void updateEntry(User user, PasswordEntry entry) throws VaultException {
        String upd = "UPDATE vault_entries SET service_name = ?, encrypted_username = ?, encrypted_password = ? WHERE id = ? AND user_id = ?";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(upd)) {
            PasswordEntry encrypted = new PasswordEntry(
                    entry.getId(), entry.getServiceName(), entry.getUsername(), entry.getPassword());
            encrypted.encrypt(EncryptionUtil.getEncryptionKey(user.getUsername()));

            pst.setString(1, entry.getServiceName());
            pst.setString(2, encrypted.getUsername());
            pst.setString(3, encrypted.getPassword());
            pst.setInt(4, entry.getId());
            pst.setInt(5, user.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new VaultException("Update failed: " + e.getMessage());
        } catch (Exception e2) {
            throw new VaultException("Encryption error: " + e2.getMessage());
        }
    }
}