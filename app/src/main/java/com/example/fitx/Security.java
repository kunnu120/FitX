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
    private static final int SALT_LENGTH = 64;
    private static final String PROVIDER = "PBKDF2withHmacSHA1";
    private static final Random RANDOM = new SecureRandom();
    private static final String IV = "ThisIVisntSecure";

    private static SecretKey localKey;
    
    // This generates a random cryptographically secure salt byte array.
    public static byte[] generateRandomSalt() {
        byte[] sBytes = new byte[SALT_LENGTH];
        RANDOM.nextBytes(sBytes);
        return sBytes;
    }

    // Encodes a byte array into base64 string for storage in database
    public static String encB64(byte[] data) {
        return Base64.encodeToString(data,Base64.DEFAULT);
    }
    
    // Decodes base64 string into byte array
    public static byte[] decB64(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

    // Generates the localKey based on the salt stored in the database and the user-entered password.
    // The localKey will be the same every time for a given user.
    public static void generateKey(byte[] salt, String password) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory keyfact = SecretKeyFactory.getInstance(PROVIDER);
            localKey = keyfact.generateSecret(spec);
        } catch (Exception e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        }
    }

    // Encrypts a string with AES using a given salt and using the localkey as a password
    public static byte[] encryptData(String data, byte[] salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance(PROVIDER);
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
    
    // Encrypts a string with a randomly generated salt, concatenates the result with the salt
    public static byte[] generateSaltCipher(String data) {
        byte[] salt = Security.generateRandomSalt();
        byte[] cipher = Security.encryptData(data,salt);
        byte[] enc = new byte[salt.length + cipher.length];
        System.arraycopy(salt, 0, enc, 0, salt.length);
        System.arraycopy(cipher, 0, enc, salt.length, cipher.length);
        return enc;
    }
    
    // Decrypts a byte array using a given salt and using the localKey as a password
    public static String decryptData(byte[] data, byte[] salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance(PROVIDER);
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
    
    // Decodes a byte array that was generated using generateSaltCipher
    public static String decodeSaltCipher(byte[] data) {
        int cipherLength = data.length - SALT_LENGTH;
        byte[] salt = new byte[SALT_LENGTH];
        byte[] cipher = new byte[cipherLength];
        System.arraycopy(data,0,salt,0,SALT_LENGTH);
        System.arraycopy(data,SALT_LENGTH,cipher,0,cipherLength);
        return decryptData(cipher,salt);
    }

    // convenience function
    public static String decode(String s) {
        return decodeSaltCipher(decB64(s));
    }
    
    // convenience function
    public static String encode(String s) {
        return encB64(Security.generateSaltCipher(s));
    }

}
