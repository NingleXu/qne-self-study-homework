package cn.ningle.shellsimulator;

import cn.ningle.shellsimulator.command.Command;
import cn.ningle.shellsimulator.command.CommandFactory;
import cn.ningle.shellsimulator.utils.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Collection;

/**
 * @author ningle
 * @version : CommandHandler.java, v 0.1 2024/06/24 11:36 ningle
 * <p>
 * 命令处理器
 * 1. 接受命令字符串解析并创建命令链
 * 2. 执行命令链 得出结果
 **/
public class CommandHandler {

    public static void handle(String command) {
        // 解析命令 生成命令执行链
        Command commandChain = parseCommend(command);
        // 执行命令
        Collection<String> executeResult = commandChain.execute(new CommandExecuteContext());
        // 输出结果
        for (String str : executeResult) {
            System.out.println(str);
        }
    }

    private static Command parseCommend(String command) {
        Preconditions.checkArgument(StringUtils.isNotNullAndNotWhitespace(command), "command is null or empty");

        String[] commandArray = command.split("\\|");

        Command firstCommand = null, preCommand = null;

        // 构建命令链
        for (String singleCommand : commandArray) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(singleCommand), "invalid command");
            if (firstCommand == null) {
                firstCommand = CommandFactory.buildCommand(singleCommand);
                preCommand = firstCommand;
                continue;
            }
            Command nextCommand = CommandFactory.buildCommand(singleCommand);
            preCommand.setNextCommand(nextCommand);
            preCommand = nextCommand;
        }

        return firstCommand;
    }

}
