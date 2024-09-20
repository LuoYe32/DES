import java.util.Arrays;

public class KeyExpansion {
    private final int[] PC1 = {            // Таблицы перестановок
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    private final int[] PC2 = {
            14, 17, 11, 24, 1, 5, 3, 28,
            15, 6, 21, 10, 23, 19, 12, 4,
            26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40,
            51, 45, 33, 48, 44, 49, 39, 56,
            34, 53, 46, 42, 50, 36, 29, 32
    };

    // Сдвиги
    private final int[] shifts = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    public byte[][] generateSubkeys(byte[] key) {
        byte[][] subkeys = new byte[16][48];   // 16 подключей по 48 бит
        byte[] permutedKey = permute(key, PC1);  // Первичная перестановка ключа

        // Ключ делится на две части
        byte[] C = Arrays.copyOfRange(permutedKey, 0, permutedKey.length / 2);
        byte[] D = Arrays.copyOfRange(permutedKey, permutedKey.length / 2, permutedKey.length);

        // Сдвиг влево, конкатенация и получение подключа на 48 бит
        for (int i = 0; i < 16; i++) {
            C = leftShift(C, shifts[i]);
            D = leftShift(D, shifts[i]);

            byte[] combined = concatenate(C, D);
            subkeys[i] = permute(combined, PC2);
        }

        return subkeys;
    }

    private byte[] permute(byte[] input, int[] table) {
        byte[] output = new byte[table.length];
        for (int i = 0; i < table.length; i++) {
//            output[i] = input[table[i] - 1];
            int bitPosition = table[i] - 1;  // Индексы таблицы начинаются с 1
            int byteIndex = bitPosition / 8;  // Индекс байта
            int bitIndex = bitPosition % 8;   // Индекс бита

            // Извлечение бита из байта
            int bit = (input[byteIndex] >> (7 - bitIndex)) & 0x1;

            // Запись бита в правильную позицию в выходном массиве
            int outputByteIndex = i / 8;
            int outputBitIndex = i % 8;

            output[outputByteIndex] |= (byte) (bit << (7 - outputBitIndex));
        }

        return output;
    }

    private byte[] leftShift(byte[] input, int shiftAmount) {
        byte[] shifted = new byte[input.length];
        System.arraycopy(input, shiftAmount, shifted, 0, input.length - shiftAmount);
        System.arraycopy(input, 0, shifted, input.length - shiftAmount, shiftAmount);

        return shifted;
    }

    private byte[] concatenate(byte[] C, byte[] D) {
        byte[] combined = new byte[C.length + D.length];
        System.arraycopy(C, 0, combined, 0, C.length);
        System.arraycopy(D, 0, combined, C.length, D.length);

        return combined;
    }
}
