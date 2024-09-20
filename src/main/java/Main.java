import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String sBoxesFilePath = "src/main/resources/s_boxes.json";
        FeistelCipher cipher = new FeistelCipher(sBoxesFilePath);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите текст для шифрования: ");
        String inputText = scanner.nextLine();
        byte[] plaintext = inputText.getBytes(StandardCharsets.UTF_8); // Преобразуем строку в массив байтов

        if (plaintext.length < 8) {
            System.out.println("Ошибка: текст должен быть не менее 8 символов.");
            return;
        } else if (plaintext.length > 8) {
            // Урезаем до 8 байтов, если ввод превышает 8 байтов
            plaintext = Arrays.copyOf(plaintext, 8);
        }

        String keyString = "abcdefgh";
        byte[] key = KeyGenerator.generateKeyFromString(keyString);

        byte[] ciphertext = cipher.encrypt(plaintext, key);
        byte[] decryptedText = cipher.decrypt(ciphertext, key);

        System.out.println("Ciphertext: " + formatByteArray(ciphertext));

        String encryptedString = new String(ciphertext, StandardCharsets.UTF_8);
        System.out.println("Encrypted string: " + encryptedString + "\n");

        System.out.println("Decrypted text: " + formatByteArray(decryptedText));

        String decryptedString = new String(decryptedText, StandardCharsets.UTF_8);
        System.out.println("Decrypted string: " + decryptedString);
    }

    private static String formatByteArray(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(b).append(" ");  // Добавляем байт и пробел
        }
        return sb.toString().trim();  // Убираем последний пробел
    }
}
