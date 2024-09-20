import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class KeyGenerator {

    public static byte[] generateKeyFromString(String keyString) {
        byte[] key = keyString.getBytes(StandardCharsets.UTF_8);
        if (key.length != 8) {
            throw new IllegalArgumentException("Key must be exactly 8 bytes long!");
        }
        return key;
    }

    public static byte[] generateRandomKey() {
        byte[] key = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }
}
