package com.securevault.model;

public abstract class VaultItem implements Secureable {
    protected int id;
    protected String serviceName;
    protected String username;
    protected String password;

    public VaultItem(int id, String serviceName, String username, String password) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }
    public int getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public abstract void encrypt(String key) throws Exception;
    public abstract void decrypt(String key) throws Exception;
}