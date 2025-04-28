package com.securevault.model;

import com.securevault.utils.EncryptionUtil;

/**
 * A concrete vault item for password storage.
 * Demonstrates polymorphism for different types of entries (could expand to notes, OTP, etc).
 */
public class PasswordEntry extends VaultItem {
    public PasswordEntry(int id, String serviceName, String username, String password) {
        super(id, serviceName, username, password);
    }
    // Encrypt username and password fields using key
    @Override
    public void encrypt(String key) throws Exception {
        this.username = EncryptionUtil.encrypt(username, key);
        this.password = EncryptionUtil.encrypt(password, key);
    }
    // Decrypt username and password fields using key
    @Override
    public void decrypt(String key) throws Exception {
        this.username = EncryptionUtil.decrypt(username, key);
        this.password = EncryptionUtil.decrypt(password, key);
    }
}