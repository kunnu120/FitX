package com.example.fitx;

import org.junit.Test;

import static org.junit.Assert.*;

public class SecurityTest {

    @Test
    public void SaltWorks() {
        byte[] salt1 = Security.generateRandomSalt();
        assertNotNull(salt1);
    }

    @Test
    public void EncryptionWorks() {
        byte[] salt1 = Security.generateRandomSalt();
        Security.generateKey(salt1,"password");
        String plainText = "hello";
        byte[] salt2 = Security.generateRandomSalt();
        byte[] cipher = Security.encryptData(plainText,salt2);
        String decipheredText = Security.decryptData(cipher,salt2);
        assertEquals(plainText,decipheredText);
    }

    @Test
    public void SaltCipherWorks() {
        byte[] salt1 = Security.generateRandomSalt();
        Security.generateKey(salt1,"password2");
        String plainText = "Hello2";
        byte[] enc = Security.generateSaltCipher(plainText);
        String dec = Security.decodeSaltCipher(enc);
        assertEquals(plainText,dec);
    }
}
