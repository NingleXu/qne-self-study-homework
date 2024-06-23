package cn.ningle.shellsimulator;

/**
 * @author ningle
 * @version : Command.java, v 0.1 2024/06/23 23:38 ningle
 **/
public abstract class Command {

    /**
     * 原始命令字符串
     */
    private String originCommand;

    private Command nextCommand;

    public Command(String originCommand, Command nextCommand) {
        this.originCommand = originCommand;
        this.nextCommand = nextCommand;
    }

    public String execute(String preResult) {
        // 获取待处理资源
        String pendingResource = getPendingResource(preResult);

        // 执行当前指令处理
        String executeResult = commandProcess(pendingResource);

        // 当前为最后一个指令
        if (nextCommand == null) {
            return executeResult;
        }

        // 传递给下一个指令
        return nextCommand.execute(executeResult);
    }

    protected abstract String getPendingResource(String preResult);

    protected abstract String commandProcess(String resource);

}
