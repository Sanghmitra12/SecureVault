package com.securevault.model;

public class AdminUser extends User {
    public AdminUser(int id, String username, String passwordHash) {
        super(id, username, passwordHash);
    }
    @Override
    public boolean isAdmin() { return true; }
}