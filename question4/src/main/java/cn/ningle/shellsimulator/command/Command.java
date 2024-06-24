package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.CommandExecuteContext;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author ningle
 * @version : Command.java, v 0.1 2024/06/23 23:38 ningle
 **/
public abstract class Command {

    /**
     * 原始命令字符串
     */
    protected final String originCommand;

    private Command nextCommand;

    public Command(String originCommand) {
        this.originCommand = originCommand;
    }

    public void setNextCommand(Command nextCommand) {
        this.nextCommand = nextCommand;
    }

    @Nonnull
    public Collection<String> execute(CommandExecuteContext context) {

        // 执行当前指令处理
        Collection<String> executeResult = commandProcess(context);

        context.setPreResult(executeResult);
        // 当前为最后一个指令
        if (nextCommand == null) {
            return executeResult;
        }

        // 传递给下一个指令
        return nextCommand.execute(context);
    }

    /**
     * 输入型指令 获取待被命令处理的数据
     * 1. 如果有前置命令处理结果和命令本身文件输入源， 则以命令本身文件输入源 作为该命令的资源输入
     * 2. 如果只有前置命令处理结果 或 命令本身文件输入源 ， 则以不为空的源 作为资源输入
     * 3. 如果都没有，则报错
     *
     * @param context 存储前置指令结果的上下文对象
     * @return 待命令处理的资源
     */
    @Nonnull
    protected abstract Collection<String> commandProcess(CommandExecuteContext context);

}
