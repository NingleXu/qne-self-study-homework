package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.CommandExecuteContext;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author ningle
 * @version : CatCommend.java, v 0.1 2024/06/23 23:54 ningle
 **/
public class CatCommend extends InputProcessingCommand {


    public CatCommend(String originCommand) {
        super(originCommand);
    }

    /**
     * e.g  1.cat -> null    2.cat xxx.txt -> xxx.txt
     *
     * @return 待处理源文件URI
     */
    @Nullable
    @Override
    protected String getPendingResourceFileURIByCommend() {
        String[] args = originCommand.split(" ");
        return args.length > 1 ? args[1] : null;
    }

    /**
     * cat 指令直接返回文件内容
     *
     * @param context 存储前置指令结果的上下文对象
     * @return 文件本身内容 或 前置输入内容本事
     */
    @Nonnull
    @Override
    protected Collection<String> commandProcess(CommandExecuteContext context) {
        CharSource charSource = getPendingResourceCharSourceByCommend();
        // 避免无输入
        Preconditions.checkState(null != charSource || null != context.getPreResult(), "cat command has not  content");

        try {
            return null == charSource ? context.getPreResult() : charSource.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
