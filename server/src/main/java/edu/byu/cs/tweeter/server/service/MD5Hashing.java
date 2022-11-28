package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// This is not very secure. DO NOT USE THIS IN YOUR PROJECT.
public class MD5Hashing {
    public static void main(String[] args) {
        // Given at registration
        String registerPassword = "password";
        System.out.println("Non-Hashed Password: " + registerPassword);

        // Store this in the database
        String hashedPassword = hashPassword(registerPassword);
        System.out.println("Hashed Password: " + hashedPassword);

        // Given at login
        String loginPassword = "password";
        String regeneratedPasswordToVerify = hashPassword(loginPassword);

        System.out.println("Passwords are the same: " + hashedPassword.equals(regeneratedPasswordToVerify));
    }

    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}