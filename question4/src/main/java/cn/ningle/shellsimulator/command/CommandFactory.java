package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.utils.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author ningle
 * @version : CommandFactory.java, v 0.1 2024/06/24 11:01 ningle
 * <p>
 * 命令工厂 通过命令字符串构建命令
 **/
public class CommandFactory {

    public static Command buildCommand(String command) {
        // 命令校验
        Preconditions.checkState(!Strings.isNullOrEmpty(command), "command is null or empty");
        // 校验是否空白
        Preconditions.checkState(StringUtils.isNotNullAndNotWhitespace(command), "command is all blank");
        // 标准化命令
        String standardCommend = standardizeCommand(command);

        // 以命令开头首个单词为依据 构建命令
        String firstWord = standardCommend.split(" ")[0];

        // todo 遵守开闭原则 优化 switch
        switch (firstWord) {
            case "cat":
                return new CatCommend(standardCommend);
            case "wc":
                return new WcCommend(standardCommend);
            case "grep":
                return new GrepCommend(standardCommend);
            default:
                throw new UnsupportedOperationException("unknown command: " + standardCommend);
        }
    }

    /**
     * 标准化命令
     * 1. 将命令参数之间多个空格合并为一个
     * 2. 去除命令收尾空格
     * 3. 字母小写
     * <p>
     * e.g ' cat   xx.txt  ' => 'cat xx.txt'
     *
     * @param originCommand 原始命令
     * @return 标准化命令
     */
    private static String standardizeCommand(String originCommand) {
        String standardCommend = originCommand.replaceAll("\\s+", " ");
        return standardCommend.trim().toLowerCase();
    }

}
