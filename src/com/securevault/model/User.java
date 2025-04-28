package com.securevault.model;

public abstract class User {
    protected int id;
    protected String username;
    protected String passwordHash;

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public abstract boolean isAdmin();
}
