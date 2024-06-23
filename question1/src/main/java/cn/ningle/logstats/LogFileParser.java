package cn.ningle.logstats;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ningle
 * @version : LogFileParser.java, v 0.1 2024/06/21 22:30 ningle
 * <p>
 * 日志文件解析器 通过解析文件日志文件 得出文件统计数据
 **/
public class LogFileParser {

    private final File logFile;

    private StatsResult statsResult;

    public LogFileParser(String fileUri) {
        logFile = new File(fileUri);

        // 校验文件是否存在
        Preconditions.checkArgument(logFile.exists(), logFile.getAbsolutePath() + " does not exist");

        // 校验路径是否是目录
        Preconditions.checkArgument(!logFile.isDirectory(), "can't parse directory: " + logFile.getAbsolutePath());
    }

    public StatsResult doParse() {
        // 每次解析重构结果
        statsResult = new StatsResult();

        // 包装成Guava的字符流源
        CharSource charSource = Files.asCharSource(logFile, StandardCharsets.UTF_8);

        // 按行读取并解析
        try {
            charSource.forEachLine(this::parseLine);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse log file: " + logFile.getAbsolutePath(), e);
        }

        // 计算出现频率top10的http接口
        statsResult.calculateTop10Logs();
        return statsResult;
    }

    /**
     * 解析请求日志中的一行
     *
     * @param line 请求日志中的一行的内容
     */
    private void parseLine(String line) {
        ++statsResult.totalRequestCount;

        // 一行请求日志的首个空格索引
        final int firstBlankIdx = line.indexOf(" ");
        Preconditions.checkArgument(firstBlankIdx != -1, "can't parse line: " + line);
        String requestType = line.substring(0, firstBlankIdx);
        statsResult.requestTypeCountMap.merge(requestType, 1L, Long::sum);

        // 依据URI格式分类
        // 首个问号出现索引
        final int firstQuestionMarkIdx = line.indexOf("?");
        // 如果不存在? 则空格后皆为请求URI
        String requestURIStr = (firstQuestionMarkIdx == -1)
                ? line.substring(firstBlankIdx + 1)
                : line.substring(firstBlankIdx + 1, firstQuestionMarkIdx);

        // 获取URI开头首个/AAA作为类型判断
        String[] URIResource = requestURIStr.split("/");
        // 校验URI是否正确
        Preconditions.checkArgument(URIResource.length >= 2, "invalid request URI: " + requestURIStr);

        // [0] = "" [1] = "AAA"
        statsResult.requestURIMap.put(URIResource[1], requestURIStr);

        // 记录每个请求接口的出现次数
        statsResult.requestURICountSet.add(line);
    }


    /**
     * @author ningle
     * @version : StatsResult.java, v 0.1 2024/06/21 22:47 ningle
     **/
    public static class StatsResult {

        // 请求总量
        private Long totalRequestCount = 0L;

        // 各种请求类型的请求总量
        private final Map<String, Long> requestTypeCountMap = new HashMap<>();

        // URI类型(/AAA/BBB 或者 /AAA/BBB/CCC)按 AAA 为 Key, URI 为 Value 的map集合
        private final Multimap<String, String> requestURIMap = LinkedHashMultimap.create();

        // 记录HTTP 接口的请求数量
        private final TreeMultiset<String> requestURICountSet = TreeMultiset.create();

        // 频率最高的前 10 个元素
        private List<Multiset.Entry<String>> top10Logs;

        /**
         * 依据requestURICountSet计算得出现频率top10的http请求接口
         */
        private void calculateTop10Logs() {
            // 计算最频繁的10 个 HTTP 接口，及其相应的请求数量
            // 创建一个列表来存储排序后的日志
            List<Multiset.Entry<String>> sortedLogs = new ArrayList<>(requestURICountSet.entrySet());
            // 按出现频率排序
            sortedLogs.sort(Comparator.comparingInt((Multiset.Entry<String> entry) -> -entry.getCount())
                    .thenComparing(Multiset.Entry::getElement));
            // 获取出现频率最高的前 10 个元素
            top10Logs = sortedLogs.subList(0, Math.min(10, sortedLogs.size()));
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();

            // 总量数据
            str.append(String.format("请求总量: %d\n", totalRequestCount));
            str.append(String.format("GET 请求总量: %d\n", requestTypeCountMap.getOrDefault("GET", 0L)));
            str.append(String.format("POST 请求总量: %d\n\n", requestTypeCountMap.getOrDefault("POST", 0L)));

            // 输出 top 10 logs
            str.append("频率最高的前 10 个 HTTP 接口:\n");
            str.append(String.format("%-5s %-80s %-10s\n", "序号", "HTTP 接口", "次数"));
            str.append("----------------------------------------------------------" +
                    "-----------------------------------------------------\n");
            if (top10Logs != null) {
                for (int i = 0; i < top10Logs.size(); i++) {
                    Multiset.Entry<String> entry = top10Logs.get(i);
                    str.append(String.format("%-5d %-80s %-10d\n", i + 1, entry.getElement(), entry.getCount()));
                }
            } else {
                str.append("无数据\n");
            }

            str.append("\n");

            // 输出 requestURIMap
            str.append("各类别URL:\n");
            str.append(String.format("%-20s %-80s\n", "类型", "URI"));
            str.append("----------------------------------------------------------" +
                    "-----------------------------------------\n");
            for (Map.Entry<String, Collection<String>> entry : requestURIMap.asMap().entrySet()) {
                str.append(String.format("%-20s %-80s\n", entry.getKey(), entry.getValue()));
            }

            return str.toString();
        }
    }

}
