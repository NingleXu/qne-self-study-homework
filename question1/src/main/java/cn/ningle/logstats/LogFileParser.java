package cn.ningle.logstats;

import java.io.File;

/**
 * @author ningle
 * @version : LogFileParser.java, v 0.1 2024/06/21 22:30 ningle
 * <p>
 * 日志文件解析器 通过解析文件日志文件 得出文件统计数据
 **/
public class LogFileParser {

    private final File logFile;

    public LogFileParser(String fileUri) {
        logFile = new File(fileUri);
        // 校验文件是否存在
        if (!logFile.exists()) {
            throw new IllegalArgumentException(logFile.getAbsolutePath() + " does not exist");
        }
        // 校验路径是否是目录
        if (logFile.isDirectory()) {
            throw new IllegalArgumentException("can't parse directory: " + logFile.getAbsolutePath());
        }
    }


}
