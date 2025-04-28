package com.securevault.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    // Hash only password
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] result = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash error: " + e.getMessage());
        }
    }

    // 🔥 Hash password + salt (NEW METHOD)
    public static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes()); // Pehle salt add karo
            byte[] result = md.digest(password.getBytes()); // Fir password digest karo
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash error: " + e.getMessage());
        }
    }
}




















// package com.securevault.utils;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// public class PasswordHasher {
// public static String hash(String password) {
// try {
// MessageDigest md = MessageDigest.getInstance("SHA-256");
// byte[] result = md.digest(password.getBytes());
// StringBuilder sb = new StringBuilder();
// for (byte b : result) sb.append(String.format("%02x", b));
// return sb.toString();
// } catch (NoSuchAlgorithmException e) {
// throw new RuntimeException("Hash error: " + e.getMessage());
// }
// }
// }


// package com.securevault.utils;

// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;

// public class PasswordHasher {

//     // New method for hashing with salt
//     public static String hash(String password, String salt) throws NoSuchAlgorithmException {
//         MessageDigest md = MessageDigest.getInstance("SHA-256");
//         md.update(Base64.getDecoder().decode(salt)); // salt use karo
//         byte[] hashedPassword = md.digest(password.getBytes());
//         return Base64.getEncoder().encodeToString(hashedPassword);
//     }

//     // Existing simple hash function (if needed)
//     public static String hash(String password) throws NoSuchAlgorithmException {
//         MessageDigest md = MessageDigest.getInstance("SHA-256");
//         byte[] hashedPassword = md.digest(password.getBytes());
//         return Base64.getEncoder().encodeToString(hashedPassword);
//     }
// }
