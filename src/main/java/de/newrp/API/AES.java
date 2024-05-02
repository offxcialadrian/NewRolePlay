package de.newrp.API;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    public static String encode(String s) throws Exception {
        String keyStr = "3JPbtwhZKmwMB7hhLL5v";
        byte[] key = (keyStr).getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("MD5");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(s.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
