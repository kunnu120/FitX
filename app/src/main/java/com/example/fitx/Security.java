package com.example.fitx;

import android.app.Application;
import android.util.Base64;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Security extends Application {
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;
    private static final Random RANDOM = new SecureRandom();
    private static final String IV = "ThisIVisntSecure";

    private static SecretKey localKey;

    public static byte[] generateRandomSalt() {
        byte[] sBytes = new byte[64];
        RANDOM.nextBytes(sBytes);
        return sBytes;
    }

    public static String encB64(byte[] data) {
        return Base64.encodeToString(data,Base64.DEFAULT);
    }

    public static byte[] decB64(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

    public static void generateKey(byte[] salt, String password) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory keyfact = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            localKey = keyfact.generateSecret(spec);
        } catch (Exception e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        }
    }

    //plainText data
    public static byte[] encryptData(String data, byte[] salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            char[] pass = (new String(localKey.getEncoded(), "UTF-8")).toCharArray();
            PBEKeySpec spec = new PBEKeySpec(pass,salt,ITERATIONS,KEY_LENGTH);
            SecretKey tmp = keyFact.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new AssertionError("Error while Encrypting: " + e.getMessage(), e);
        }
    }

    public static String decryptData(byte[] data, byte[] salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            char[] pass = (new String(localKey.getEncoded(), "UTF-8")).toCharArray();
            PBEKeySpec spec = new PBEKeySpec(pass, salt, ITERATIONS, KEY_LENGTH);
            SecretKey tmp = keyFact.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            throw new AssertionError("Error while Decrypting: " + e.getMessage(), e);
        }
    }
}
