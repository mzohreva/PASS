package pass.core.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MasterKey
{

    private static final Logger LOGGER = Logger.getLogger(MasterKey.class.getName());

    private static final MasterKey instance = new MasterKey();

    public static MasterKey getInstance()
    {
        return instance;
    }

    private String key;

    private MasterKey()
    {
        key = null;
    }

    private void readKeyFromFile(String path)
    {
        try {
            Path p = Paths.get(path);
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            key = lines.get(0);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not read master key", ex);
        }
    }

    // Tunable parameters
    private static final int SALT_BYTES = 16;
    private static final int PBKDF_ITERATIONS = 75000;
    // Fixed parameters
    private static final int AES_KEY_BYTES = 16;    // using AES 128
    private static final int GCM_TAG_BYTES = 16;
    private static final int GCM_IV_BYTES = 16;
    private static final String PBKDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CRYPTO_ALGORITHM = "AES/GCM/NoPadding";
    private static final String CRYPTO_CIPHER = "AES";

    public String decrypt(String input)
    {
        if (key == null) {
            readKeyFromFile(Config.getInstance().getMasterKeyPath());
        }

        byte[] inputBytes = Base64.getUrlDecoder().decode(input);
        if (inputBytes.length < SALT_BYTES + GCM_TAG_BYTES) {
            return null;
        }
        byte[] salt = Arrays.copyOfRange(inputBytes, 0, SALT_BYTES);
        byte[] cipherTextBytes = Arrays.copyOfRange(inputBytes, SALT_BYTES, inputBytes.length);

        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
            KeySpec spec = new PBEKeySpec(key.toCharArray(),
                                          salt,
                                          PBKDF_ITERATIONS,
                                          8 * (AES_KEY_BYTES + GCM_IV_BYTES));
            SecretKey derivedKey = keyFactory.generateSecret(spec);
            byte[] derivedKeyBytes = derivedKey.getEncoded();
            byte[] aesKeyMaterial = Arrays.copyOfRange(derivedKeyBytes, 0, AES_KEY_BYTES);
            byte[] gcmIvMaterial = Arrays.copyOfRange(derivedKeyBytes, AES_KEY_BYTES, derivedKeyBytes.length);
            SecretKey aesKey = new SecretKeySpec(aesKeyMaterial, CRYPTO_CIPHER);
            GCMParameterSpec gcmParams = new GCMParameterSpec(8 * GCM_TAG_BYTES, gcmIvMaterial);
            Cipher decryptor = Cipher.getInstance(CRYPTO_ALGORITHM);
            decryptor.init(Cipher.DECRYPT_MODE, aesKey, gcmParams);
            byte[] plainTextBytes = decryptor.doFinal(cipherTextBytes);
            String plainText = new String(plainTextBytes, StandardCharsets.UTF_8);
            return plainText;
        }
        catch (NoSuchAlgorithmException
               | NoSuchPaddingException
               | InvalidKeySpecException
               | InvalidKeyException
               | InvalidAlgorithmParameterException
               | IllegalBlockSizeException
               | BadPaddingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String encrypt(String key, String data)
    {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        rand.nextBytes(salt);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
            KeySpec spec = new PBEKeySpec(key.toCharArray(),
                                          salt,
                                          PBKDF_ITERATIONS,
                                          8 * (AES_KEY_BYTES + GCM_IV_BYTES));
            SecretKey derivedKey = keyFactory.generateSecret(spec);
            byte[] derivedKeyBytes = derivedKey.getEncoded();
            byte[] aesKeyMaterial = Arrays.copyOfRange(derivedKeyBytes, 0, AES_KEY_BYTES);
            byte[] gcmIvMaterial = Arrays.copyOfRange(derivedKeyBytes, AES_KEY_BYTES, derivedKeyBytes.length);
            SecretKey aesKey = new SecretKeySpec(aesKeyMaterial, CRYPTO_CIPHER);
            GCMParameterSpec gcmParams = new GCMParameterSpec(8 * GCM_TAG_BYTES, gcmIvMaterial);
            Cipher encryptor = Cipher.getInstance(CRYPTO_ALGORITHM);
            encryptor.init(Cipher.ENCRYPT_MODE, aesKey, gcmParams);
            byte[] inputBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] cipherTextBytes = encryptor.doFinal(inputBytes);
            byte[] output = new byte[salt.length + cipherTextBytes.length];
            System.arraycopy(salt, 0, output, 0, salt.length);
            System.arraycopy(cipherTextBytes, 0, output, salt.length, cipherTextBytes.length);
            return Base64.getUrlEncoder().encodeToString(output);
        }
        catch (NoSuchAlgorithmException
               | NoSuchPaddingException
               | InvalidKeySpecException
               | InvalidKeyException
               | InvalidAlgorithmParameterException
               | IllegalBlockSizeException
               | BadPaddingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            System.out.println("Error: " + ex.toString());
            return null;
        }
    }
}
