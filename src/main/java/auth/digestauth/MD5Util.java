package auth.digestauth;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String getMD5Hash(byte[] text_bytes) throws NoSuchAlgorithmException {
        final char[] HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        int n = text_bytes.length;
        char[] buffer = new char[n * 2];

        for(int i = 0; i < n; ++i) {
            int low = text_bytes[i] & 15;
            int high = (text_bytes[i] & 240) >> 4;
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[i * 2 + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);    }
}
