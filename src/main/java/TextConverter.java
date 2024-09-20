import java.nio.charset.StandardCharsets;

public class TextConverter {

    public static byte[] stringToBytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
