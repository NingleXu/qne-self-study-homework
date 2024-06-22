package cn.ningle.codecheck;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author ningle
 * @version : EffectiveLineCounter.java, v 0.1 2024/06/22 19:43 ningle
 * 有效代码行数计数器
 **/
public class EffectiveLineCounter {

    public static int doStats(String fileURL) {
        CharSource fileCharSource = checkFileAndGetCharSource(fileURL);

        // 利用context储存解析过程的数据
        EffectiveLineParseContext effectiveLineParseContext = new EffectiveLineParseContext();

        // 对代码进行逐行解析与判断
        try {
            fileCharSource.forEachLine(line -> parseSingleLine(line, effectiveLineParseContext));
        } catch (Exception e) {
            throw new RuntimeException("parse error!", e);
        }

        // 统计完毕 返回有效代码数
        return effectiveLineParseContext.effectiveLineCount;
    }


    private static void parseSingleLine(String line, EffectiveLineParseContext parseContext) {
        // 忽略空行、空白行
        if (Strings.isNullOrEmpty(line) || line.trim().isEmpty()) return;
        // 去除首位空格
        line = line.trim();
        // 忽略单行注解
        if (line.startsWith("//")) return;

        // 该行为多行注释结尾则关闭多行注释
        if (line.endsWith("*/")) {
            parseContext.closeMultiComment();
            return;
        }

        // 该行处于多行注释中 忽略
        if (parseContext.isBetweenMultiComment) return;

        // 该行为开启多行注释
        if (line.startsWith("/*")) {
            parseContext.openMultiComment();
            return;
        }

        // 该行为有效代码
        ++parseContext.effectiveLineCount;
    }

    /**
     * 校验文件路径并返回 字节源
     *
     * @param fileURL 文件路径
     * @return 文件字节源
     */
    private static CharSource checkFileAndGetCharSource(String fileURL) {
        File file = new File(fileURL);
        // 校验文件是否存在
        Preconditions.checkArgument(file.exists(), "File does not exist: " + fileURL);
        // 校验是否为文件夹
        Preconditions.checkArgument(file.isFile(), "File is not a file: " + fileURL);

        return Files.asCharSource(file, StandardCharsets.UTF_8);
    }

    private static class EffectiveLineParseContext {

        // 有效代码行数，理论上不超过Integer.MAX_VALUE
        private int effectiveLineCount = 0;

        // 是否处于多行注释范围内
        private boolean isBetweenMultiComment = false;

        private void openMultiComment() {
            isBetweenMultiComment = true;
        }

        private void closeMultiComment() {
            isBetweenMultiComment = false;
        }
    }
}
