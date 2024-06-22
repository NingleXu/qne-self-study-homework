package cn.ningle.logstats;

import java.io.IOException;

/**
 * @author ningle
 * @version : Main.java, v 0.1 2024/06/21 22:27 ningle
 **/
public class Main {
    public static void main(String[] args) throws IOException {
        // 读取文件并解析
        LogFileParser.StatsResult statsResult = new LogFileParser("attachments/Question 1/access.log").doParse();
        // 输出结果
        System.out.println(statsResult);
    }
}
