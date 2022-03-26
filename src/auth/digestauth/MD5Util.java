package auth.digestauth;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String getMD5Hash(byte[] text_bytes) throws NoSuchAlgorithmException {
        return String.format("%032x", new BigInteger(1, text_bytes));
    }
}
