package com.securevault.model;

public class NormalUser extends User {
    public NormalUser(int id, String username, String passwordHash) {
        super(id, username, passwordHash);
    }
    @Override
    public boolean isAdmin() { return false; }
}