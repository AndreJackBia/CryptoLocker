package com.example.biagg.cryptolocker;

import android.util.Log;

import com.example.thekeymaker.cryptolocker.Cryptos;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException nsae) {
        }

        md.update("string".getBytes());
        byte[] digest = md.digest();
        System.out.println(Arrays.toString(digest));

        md.reset();

        md.update("string".getBytes());
        byte[] digest2 = md.digest();
        System.out.println(Arrays.toString(digest));
        assertEquals(4, 2 + 2);
    }
}