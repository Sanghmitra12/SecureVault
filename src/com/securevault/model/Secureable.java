package com.securevault.model;

/**
 * Interface for encryption/decryption of vault items
 */
public interface Secureable {
    public void encrypt(String key) throws Exception;
    public void decrypt(String key) throws Exception;
}