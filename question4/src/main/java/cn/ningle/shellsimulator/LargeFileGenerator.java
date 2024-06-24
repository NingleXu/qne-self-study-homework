package cn.ningle.shellsimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class LargeFileGenerator {
    public static void main(String[] args) {
        String filePath = "largeTestFile.txt";
        long targetFileSize = 100 * 1024 * 1024; // 100MB
        int maxLineLength = 100; // 每行的最大长度
        generateLargeFile(filePath, targetFileSize, maxLineLength);
    }

    public static void generateLargeFile(String filePath, long targetFileSize, int maxLineLength) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            Random random = new Random();
            long fileSize = 0;

            while (fileSize < targetFileSize) {
                int lineLength = random.nextInt(maxLineLength) + 1;
                StringBuilder line = new StringBuilder(lineLength);

                for (int i = 0; i < lineLength; i++) {
                    line.append((char) ('a' + random.nextInt(26))); // 生成随机字符
                }

                writer.write(line.toString());
                writer.newLine();
                fileSize += line.length() + System.lineSeparator().length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
