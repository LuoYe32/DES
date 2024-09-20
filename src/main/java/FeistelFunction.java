import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.IOException;

public class FeistelFunction {
    private final int[] expansionTable = {
            32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
    };

    private int[][][] sBoxes;

    private final int[] permutationTable = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    public FeistelFunction(String sBoxesFilePath) {
        try {
            this.sBoxes = loadSBoxesFromJson(sBoxesFilePath);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        }
    }

    private int[][][] loadSBoxesFromJson(String filePath) throws IOException {
        Gson gson = new Gson();
        SBoxesData data = gson.fromJson(new FileReader(filePath), SBoxesData.class);
        return data.getsBoxes();
    }

    public byte[] feistel(byte[] rightPart, byte[] subkey) {
        byte[] expandedRight = permute(rightPart, expansionTable);  // Расширяем до 48 бит
        byte[] xorResult = xor(expandedRight, subkey);  // XOR с подключом

        byte[] sBoxOutput = applySBoxes(xorResult);  // Применяем S блоки
        return permute(sBoxOutput, permutationTable);  // Перестановка
    }

    private byte[] permute(byte[] input, int[] table) {
        // Преобразуем входные байты в массив битов
        boolean[] bits = new boolean[8 * input.length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (input[i] & (1 << (7 - j))) != 0;
            }
        }

        // Применяем таблицу перестановки
        boolean[] permutedBits = new boolean[table.length];
        for (int i = 0; i < table.length; i++) {
            permutedBits[i] = bits[table[i] - 1];  // Используем индексы таблицы
        }

        // Преобразуем обратно в байты
        byte[] output = new byte[(permutedBits.length + 7) / 8];  // Количество байтов
        for (int i = 0; i < permutedBits.length; i++) {
            if (permutedBits[i]) {
                output[i / 8] |= (byte) (1 << (7 - (i % 8)));  // Устанавливаем соответствующий бит
            }
        }

        return output;
    }


    private byte[] xor(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = (byte) (array1[i] ^ array2[i]);
        }
        return result;
    }

    private byte[] applySBoxes(byte[] input) {
        byte[] output = new byte[32];  // Выход после S-блоков 32 бита

        // Преобразуем байты в массив битов
        boolean[] bits = new boolean[input.length * 8];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (input[i] >> (7 - j) & 0x1) == 1;  // Заполняем массив битов
            }
        }

        // Преобразование через S-блоки
        for (int i = 0; i < 8; i++) {
            int row = ((bits[i * 6] ? 1 : 0) << 1) | (bits[i * 6 + 5] ? 1 : 0);  // Получаем строку
            int col = ((bits[i * 6 + 1] ? 1 : 0) << 3) | (bits[i * 6 + 2] ? 1 : 0) << 2 |
                    (bits[i * 6 + 3] ? 1 : 0) << 1 | (bits[i * 6 + 4] ? 1 : 0);  // Столбец
            int sBoxValue = sBoxes[i][row][col];  // Преобразуем значение через S-блок

            // Конвертируем значение S-блока в 4-битное представление
            for (int j = 0; j < 4; j++) {
                output[i * 4 + j] = (byte) ((sBoxValue >> (3 - j)) & 0x1);
            }
        }

        return output;
    }

}

class SBoxesData {
    private int[][][] sBoxes;

    public int[][][] getsBoxes() {
        return sBoxes;
    }

    public void setsBoxes(int[][][] sBoxes) {
        this.sBoxes = sBoxes;
    }
}