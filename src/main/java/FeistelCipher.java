import java.util.Arrays;

public class FeistelCipher {
    private KeyExpansion keyExpansion = new KeyExpansion();
    private FeistelFunction feistelFunction;

    public FeistelCipher(String sBoxesFilePath) {
        this.feistelFunction = new FeistelFunction(sBoxesFilePath);
    }

    public byte[] encrypt(byte[] plainText, byte[] key) {
        byte[][] subkeys = keyExpansion.generateSubkeys(key);

        byte[] leftPart = Arrays.copyOfRange(plainText, 0, 4);
        byte[] rightPart = Arrays.copyOfRange(plainText, 4, 8);

        for (int i = 0; i < 16; i++) {
            byte[] tempRight = rightPart;
            rightPart = xor(leftPart, feistelFunction.feistel(rightPart, subkeys[i]));
            leftPart = tempRight;
        }

        return concatenate(rightPart, leftPart);  // Меняем местами в конце
    }

    public byte[] decrypt(byte[] cipherText, byte[] key) {
        byte[][] subkeys = keyExpansion.generateSubkeys(key);

        byte[] leftPart = Arrays.copyOfRange(cipherText, 0, 4);
        byte[] rightPart = Arrays.copyOfRange(cipherText, 4, 8);

        for (int i = 15; i >= 0; i--) {
            byte[] tempRight = rightPart;
            rightPart = xor(leftPart, feistelFunction.feistel(rightPart, subkeys[i]));
            leftPart = tempRight;
        }

        return concatenate(rightPart, leftPart);  // Меняем местами в конце
    }

    private byte[] xor(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = (byte) (array1[i] ^ array2[i]);
        }
        return result;
    }

    private byte[] concatenate(byte[] left, byte[] right) {
        byte[] combined = new byte[left.length + right.length];
        System.arraycopy(left, 0, combined, 0, left.length);
        System.arraycopy(right, 0, combined, left.length, right.length);
        return combined;
    }
}
