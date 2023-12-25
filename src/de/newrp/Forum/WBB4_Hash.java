package de.newrp.Forum;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;

public class WBB4_Hash {
    public static final String BCRYPT_COST = "08";
    public static final String BCRYPT_TYPE = "2a";
    static Random rn;
    private static String blowfishCharacters;

    static {
        WBB4_Hash.blowfishCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789./";
        WBB4_Hash.rn = new Random();
    }

    public static String getDoubleSaltedHash(final String password, final String salt) {
        return getSaltedHash(getSaltedHash(password, salt), salt);
    }

    public static String getSaltedHash(final String password, final String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public static String getRandomSalt() {
        StringBuilder salt = new StringBuilder();
        final int maxIndex = WBB4_Hash.blowfishCharacters.length() - 1;
        for (int i = 0; i < 22; ++i) {
            salt.append(WBB4_Hash.blowfishCharacters.charAt(secureRandomNumber(0, maxIndex)));
        }
        return getSalt(salt.toString());
    }

    public static String getSalt(String salt) {
        salt = salt.substring(0, 22);
        return "$2a$08$" + salt;
    }

    public static int secureRandomNumber(final int min, final int max) {
        SecureRandom secureRandomGenerator = null;
        try {
            secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        final int range = max - min;
        if (range == 0) {
            System.out.println("[BurningBoard Bridge] Error(#0XX) > Cannot generate a secure random number, min and max are the same");
        }
        final double log = Math.log(range) / Math.log(2.0);
        final int bytes = (int) (log / 8.0) + 1;
        final int bits = (int) log + 1;
        final int filter = (1 << bits) - 1;
        int rnd;
        do {
            final byte[] randomBytes = new byte[bytes];
            secureRandomGenerator.nextBytes(randomBytes);
            rnd = (Integer.parseInt(bin2hex(randomBytes), 16) & filter);
        } while (rnd >= range);
        return min + rnd;
    }

    public static String bin2hex(final byte[] data) {
        return String.format("%0" + data.length * 2 + "x", new BigInteger(1, data));
    }

    @SuppressWarnings("resource")
    public static String getAccessToken() {
        final String token = String.valueOf(System.currentTimeMillis()) + WBB4_Hash.rn.nextInt();
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] encryptMsgPassWd = md.digest(token.getBytes());
            final Formatter formatterToken = new Formatter();
            byte[] array;
            for (int length = (array = encryptMsgPassWd).length, i = 0; i < length; ++i) {
                final byte b = array[i];
                formatterToken.format("%02x", b);
            }
            return formatterToken.toString();
        } catch (Exception e) {
            System.out.println("[BurningBoard Bridge] Error(#004.1) > Error while hashing access token: " + e);
            return null;
        }
    }
}
