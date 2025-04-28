package com.securevault.service;

import com.securevault.model.AdminUser;
import com.securevault.model.NormalUser;
import com.securevault.model.User;
import com.securevault.utils.DBConnection;
import com.securevault.utils.PasswordHasher;
import com.securevault.utils.EncryptionUtil;
import com.securevault.exceptions.AuthenticationException;

import java.sql.*;

public class AuthenticationService {

    public User login(String username, String password) throws AuthenticationException {
        String sql = "SELECT id, username, password_hash, salt, is_admin FROM users WHERE username = ?";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String dbHash = rs.getString("password_hash");
                String salt = rs.getString("salt"); // ✅ Salt nikalna
                boolean isAdmin = rs.getInt("is_admin") == 1;

                String hashedPassword = PasswordHasher.hash(password, salt); // ✅ Password + salt hash

                if (dbHash.equals(hashedPassword)) { // ✅ Compare correctly
                    int userId = rs.getInt("id");
                    if (isAdmin)
                        return new AdminUser(userId, username, dbHash);
                    else
                        return new NormalUser(userId, username, dbHash);
                } else {
                    throw new AuthenticationException("Incorrect password!");
                }
            } else {
                throw new AuthenticationException("User not found!");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Database error: " + e.getMessage());
        }
    }

    public User signup(String username, String password) throws AuthenticationException {
        String insert = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            String salt = EncryptionUtil.generateSalt(); // ✅ Generate salt
            String passwordHash = PasswordHasher.hash(password, salt); // ✅ Hash password with salt

            pst.setString(1, username);
            pst.setString(2, passwordHash);
            pst.setString(3, salt);

            int affected = pst.executeUpdate();
            if (affected == 1) {
                ResultSet keys = pst.getGeneratedKeys();
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    return new NormalUser(userId, username, passwordHash);
                }
            }
            throw new AuthenticationException("Signup failed.");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new AuthenticationException("Username already exists!");
            throw new AuthenticationException("Signup error: " + e.getMessage());
        }
    }
}
