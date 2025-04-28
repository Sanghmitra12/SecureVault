package com.securevault.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utility class for encryption, decryption, and hashing
 */
public class EncryptionUtil {
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = new byte[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    };
    
    /**
     * Generate a random salt for password hashing
     * @return A base64 encoded salt string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash a password with a salt using SHA-256
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available
     */
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(Base64.getDecoder().decode(salt));
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    /**
     * Encrypt a string using AES encryption
     * @param data The string to encrypt
     * @param key The encryption key
     * @return The encrypted string
     * @throws Exception If encryption fails
     */
    public static String encrypt(String data, String key) throws Exception {
        SecretKey secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    
    /**
     * Decrypt a string using AES encryption
     * @param encryptedData The string to decrypt
     * @param key The decryption key
     * @return The decrypted string
     * @throws Exception If decryption fails
     */
    public static String decrypt(String encryptedData, String key) throws Exception {
        SecretKey secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
    
    /**
     * Generate a SecretKey from a string
     * @param key The key string
     * @return A SecretKey object
     * @throws Exception If key generation fails
     */
    private static SecretKey generateKey(String key) throws Exception {
        byte[] salt = "SecureVaultSalt".getBytes(StandardCharsets.UTF_8);
        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
 * Derive a simple 16-byte encryption key from username
 * @param username The username
 * @return The 16-byte encryption key
 */
public static String getEncryptionKey(String username) {
    String base = (username + "1234567890abcdef");
    if (base.length() < 16) {
        base = base + "1234567890abcdef";
    }
    return base.substring(0, 16);
}
}
