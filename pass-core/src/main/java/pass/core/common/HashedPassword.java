package pass.core.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashedPassword
{

    private static final String HASH_ALGORITHM = "SHA-512";
    private static final int SALT_CHARS_LENGTH = 16;

    private final String salt;
    private final byte[] hash;     // 64 bytes long for sha-512

    public HashedPassword(String salt, byte[] hash)
    {
        this.salt = salt;
        this.hash = hash;
    }

    private static byte[] calculateHash(String plainText, String salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        md.update(plainText.getBytes("UTF-8"));
        md.update(salt.getBytes("UTF-8"));
        return md.digest();
    }

    private static String generateSalt()
    {
        byte[] salt = new byte[SALT_CHARS_LENGTH * 3 / 4];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static HashedPassword generate(String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String salt = generateSalt();
        byte[] hash = calculateHash(password, salt);
        return new HashedPassword(salt, hash);
    }

    public boolean matches(String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        byte[] hashed = calculateHash(password, this.salt);
        if (hashed.length != this.hash.length) {
            return false;
        }
        for (int i = 0; i < hashed.length; i++) {
            if (hashed[i] != this.hash[i]) {
                return false;
            }
        }
        return true;
    }

    public String getSalt()
    {
        return salt;
    }

    public byte[] getHash()
    {
        return hash;
    }
}
