package com.wini.leader_board_integration.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAUtils {

    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+IN3SwvrXvcBbtKTSCoYPm9m7rPfw/A/VsmPrA29oHnR+ILkBV5gu81ZO5nxEvTq1QOt2Cb9Mp0JxxKJdeAxIrWVfrtr9/sc3tw4+juqvbargiTZm4Okc4JHEbAYW3LIJht897cSmZf8oIysZ12DbCoTrmWLnKF/wBfyderryNwIDAQAB";
    private static String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAL4g3dLC+te9wFu0pNIKhg+b2bus9/D8D9WyY+sDb2gedH4guQFXmC7zVk7mfES9OrVA63YJv0ynQnHEol14DEitZV+u2v3+xze3Dj6O6q9tquCJNmbg6RzgkcRsBhbcsgmG3z3txKZl/ygjKxnXYNsKhOuZYucoX/AF/J16uvI3AgMBAAECgYA048GxWqobD98N47+h6mmGuA0mxyQl9oiVIs3m14rLRQWU/Jdahzp/fRO6WSWinZ1SRJ/7sxRzR2gVa+Ta6YVIy33r/OO/0tLivJIssFxMgwF/fViVrmQHvWS7LO2XF5Y1ba0ZKHebfZuZrldqsmB1eNLo35rF4h1dzkYUDBjTiQJBAOkCU+KHmR+Jv4fzsGGL9Z2A2LWEK9z6uW0+8gxyE/9/VsJplD/bayzvgTawMk2yLtVRhxqLjHGu3zC5hgPwkR0CQQDQ42W9arUyx7gjk5RyTW2IWmmTWspe4qlcF2bqIlAM78mzHD7eKagdnyvjb4el1ZlqZo1HwmWVb8kY8ju0OeRjAkBb4boYYuYjxJrNTm/oF1u0TyGlekgdrJTAGokyFS4Dm0AoC6AVCj4aCxvF5/b+IlxLbAm9kve/wHhsa0wIc62VAkA4yemJ8m2qjYNxIJkV7hjeHf5f0in8W9DBOZeABa4Ppk0GhaJN0bNjwjczUMnm+sgHljPIk3/8fXSFdK1olOiTAkAQqjkvWbwCZAKsL0VcKC/d93id/1ZXbFBPyANFJsBM4eM1gdkju9vTV3SDF5AexW5G8DHaiWbX8dBNIyOQXW4+";

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public static String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(PRIVATE_KEY));
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
            String encryptedString = Base64.getEncoder().encodeToString(encrypt("971509818799|454", PUBLIC_KEY));
            System.out.println(encryptedString);
            String decryptedString = com.wini.leader_board_integration.util.RSAUtils.decrypt(encryptedString, PRIVATE_KEY);
            System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

    }
}
