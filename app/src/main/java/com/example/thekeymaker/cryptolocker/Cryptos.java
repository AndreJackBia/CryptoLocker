package com.example.thekeymaker.cryptolocker;

import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by thekeymaker on 14/03/2018.
 */

public class Cryptos {

    protected static String encrypt(String psw, String keyword) {
        String result = null;
        try {
            byte[] key = Arrays.copyOf(keyword.getBytes("UTF-8"), 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(psw.getBytes());
            result = Base64.encodeToString(encrypted, Base64.DEFAULT);

            /*
            * Old encryption
            byte[] key = keyword.getBytes("UTF-8");
            MessageDigest sha;
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(psw.getBytes());
            result = Base64.encodeToString(encrypted, Base64.DEFAULT);*/

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return result;
    }

    protected static String decrypt(String encrypted, String keyword) {
        try {
            byte[] key = Arrays.copyOf(keyword.getBytes("UTF-8"), 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
