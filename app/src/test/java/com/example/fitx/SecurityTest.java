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
        assertNotNull(salt1);
        Security.generateKey(Security.generateRandomSalt(),"password");
        String plainText = "hello";
        byte[] salt2 = Security.generateRandomSalt();
        byte[] cipher = Security.encryptData(plainText,salt2);
        String decipheredText = Security.decryptData(cipher,salt2);
        assertEquals(plainText,decipheredText);
    }
}
