package com.biubiu.stock.stockanalyze.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;

public class SqlSplitUtils {

    /**
     * 拆分 SQL 文件
     * @param inputFile   输入文件路径
     * @param outputDir   输出目录
     * @param maxSizeMb   每个文件最大大小（MB）
     */
    public static void splitSqlFile(String inputFile, String outputDir, int maxSizeMb) throws IOException {
        long maxSizeBytes = (long) maxSizeMb * 1024 * 1024;

        // 创建输出目录
        Files.createDirectories(Paths.get(outputDir));

        String baseName = Paths.get(inputFile).getFileName().toString().replace(".sql", "");

        int fileIndex = 1;
        long currentSize = 0;
        StringBuilder currentContent = new StringBuilder();
        StringBuilder headerContent = new StringBuilder();
        boolean isHeader = true;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // INSERT 语句之前的都算 header
                if (line.trim().startsWith("INSERT") && isHeader) {
                    isHeader = false;
                }

                if (isHeader) {
                    headerContent.append(line).append("\n");
                    continue;
                }

                currentContent.append(line).append("\n");
                currentSize += line.getBytes(StandardCharsets.UTF_8).length;

                // 超过大小限制就写入文件
                if (currentSize >= maxSizeBytes) {
                    writeFile(outputDir, baseName, fileIndex, headerContent, currentContent);
                    fileIndex++;
                    currentSize = 0;
                    currentContent = new StringBuilder();
                }
            }

            // 写入剩余内容
            if (currentContent.length() > 0) {
                writeFile(outputDir, baseName, fileIndex, headerContent, currentContent);
            }
        }

        System.out.println("\n拆分完成，共生成 " + fileIndex + " 个文件，输出目录: " + outputDir);
    }

    public static void writeFile(String outputDir, String baseName, int index,
                                  StringBuilder header, StringBuilder content) throws IOException {
        String outputPath = outputDir + File.separator + baseName + "_" + index + ".sql";
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            writer.write(header.toString());  // 每个文件都带上头部
            writer.write(content.toString());
        }
        long fileSize = new File(outputPath).length();
        System.out.printf("写入文件: %s (%.2f MB)%n", outputPath, fileSize / 1024.0 / 1024.0);
    }

    public static void verify(String outputDir, int maxSizeMb) {
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));

        if (files == null || files.length == 0) {
            System.out.println("没有找到文件");
            return;
        }

        long totalSize = 0;
        for (File file : files) {
            long sizeMb = file.length() / 1024 / 1024;
            totalSize += file.length();
            String status = sizeMb > maxSizeMb ? "❌ 超出限制" : "✅ 正常";
            System.out.printf("%s | %.2f MB | %s%n", file.getName(), file.length() / 1024.0 / 1024.0, status);
        }
        System.out.printf("\n共 %d 个文件，总大小: %.2f MB%n", files.length, totalSize / 1024.0 / 1024.0);
    }

    public static void verifyLineCount(String inputFile, String outputDir) throws IOException {
        // 统计原始文件行数
        long inputLines = countLines(inputFile);
        System.out.println("原始文件行数: " + inputLines);

        // 统计所有拆分文件行数
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        long totalOutputLines = 0;
        for (File file : files) {
            long lines = countLines(file.getAbsolutePath());
            totalOutputLines += lines;
            System.out.println(file.getName() + " 行数: " + lines);
        }

        System.out.println("\n原始行数: " + inputLines);
        System.out.println("拆分后总行数: " + totalOutputLines);
        // 因为每个文件都加了 header，所以拆分后总行数会比原始多
        System.out.println(totalOutputLines >= inputLines ? "✅ 行数验证通过" : "❌ 行数有丢失");
    }

    public static long countLines(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            long count = 0;
            while (reader.readLine() != null) count++;
            return count;
        }
    }

    public static void verifyContent(String inputFile, String outputDir) throws IOException {
        // 获取原始文件第一条和最后一条 INSERT
        String firstInsert = null;
        String lastInsert = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("INSERT")) {
                    if (firstInsert == null) firstInsert = line;
                    lastInsert = line;
                }
            }
        }

        System.out.println("=== 原始文件 ===");
        System.out.println("第一条INSERT: " + firstInsert);
        System.out.println("最后一条INSERT: " + lastInsert);

        // 获取拆分文件中第一个文件的第一条和最后一个文件的最后一条 INSERT
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        Arrays.sort(files, Comparator.comparing(File::getName));

        // 第一个文件的第一条 INSERT
        String splitFirstInsert = null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(files[0]), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("INSERT")) {
                    splitFirstInsert = line;
                    break;
                }
            }
        }

        // 最后一个文件的最后一条 INSERT
        String splitLastInsert = null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(files[files.length - 1]), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("INSERT")) {
                    splitLastInsert = line;
                }
            }
        }

        System.out.println("\n=== 拆分文件 ===");
        System.out.println("第一条INSERT: " + splitFirstInsert);
        System.out.println("最后一条INSERT: " + splitLastInsert);

        System.out.println("\n=== 验证结果 ===");
        System.out.println("第一条INSERT: " + (firstInsert.equals(splitFirstInsert) ? "✅ 一致" : "❌ 不一致"));
        System.out.println("最后一条INSERT: " + (lastInsert.equals(splitLastInsert) ? "✅ 一致" : "❌ 不一致"));
    }

    public static void verifyJoints(String inputFile, String outputDir) throws IOException {
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        Arrays.sort(files, Comparator.comparing(File::getName));

        for (int i = 0; i < files.length - 1; i++) {
            // 当前文件最后一条 INSERT
            String lastLine = null;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(files[i]), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("INSERT")) lastLine = line;
                }
            }

            // 下一个文件第一条 INSERT
            String firstLine = null;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(files[i + 1]), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("INSERT")) {
                        firstLine = line;
                        break;
                    }
                }
            }

            System.out.println("=== " + files[i].getName() + " → " + files[i + 1].getName() + " ===");
            System.out.println("前文件最后一条: " + lastLine);
            System.out.println("后文件第一条:   " + firstLine);
            System.out.println(lastLine != null && firstLine != null && !lastLine.equals(firstLine)
                    ? "✅ 衔接正常\n" : "❌ 衔接异常\n");
        }
    }
}
