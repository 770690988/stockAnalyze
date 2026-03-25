package com.biubiu.stock.stockanalyze.component;

import com.biubiu.stock.stockanalyze.utils.SqlSplitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
@Slf4j
public class DatabaseBackupTask {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.split-size}")
    private Integer SPLIT_SIZE = 40;

    @Value("${backup.container-name}")
    private String CONTAINER_NAME;

    @Value("${backup.db-name}")
    private String DB_NAME;

    @Value("${backup.backup-dir}")
    private String BACKUP_DIR;

    @Value("${backup.split-path}")
    private String SPLIT_PATH;

    private String[] buildCommand(String shellCommand) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            log.info("current os is windows");
            return new String[]{"cmd", "/c", shellCommand};
        } else {
            log.info("current os is linux");
            return new String[]{"sh", "-c", shellCommand};
        }
    }

    @Scheduled(cron = "0 40 15 ? * MON-FRI")
    public void backupDatabase() {
        log.info("开始备份数据库...");

        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String fileName = DB_NAME + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".sql";
        String filePath = BACKUP_DIR + File.separator + fileName;

        String[] command = buildCommand(
                String.format("docker exec %s mysqldump -u%s -p%s %s > %s",
                        CONTAINER_NAME, dbUser, dbPassword, DB_NAME, filePath)
        );

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                long sizeMb = new File(filePath).length() / 1024 / 1024;
                log.info("备份成功: {} ({} MB)", filePath, sizeMb);
                cleanOldBackups();
            } else {
                String error = new String(process.getErrorStream().readAllBytes());
                log.error("备份失败，exitCode: {}，错误: {}", exitCode, error);
            }
        } catch (Exception e) {
            log.error("备份异常: {}", e.getMessage());
        }

        try {
            splitSql(filePath);
        } catch (Exception e) {
            log.error("sql文件分割异常: {}", e.getMessage());
        }
    }

    private void cleanOldBackups() {
        File backupDir = new File(BACKUP_DIR);
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        if (files == null) return;

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        for (File file : files) {
            String dateStr = file.getName().replace(DB_NAME + "_", "").replace(".sql", "");
            try {
                LocalDate fileDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (fileDate.isBefore(thirtyDaysAgo)) {
                    file.delete();
                    log.info("删除过期备份: {}", file.getName());
                }
            } catch (Exception e) {
                log.warn("解析文件日期失败: {}", file.getName());
            }
        }
    }

    public void splitSql(String filePath) throws IOException {
        String finalSplitPath = SPLIT_PATH + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SqlSplitUtils.splitSqlFile(filePath, finalSplitPath, SPLIT_SIZE);
    }
}
