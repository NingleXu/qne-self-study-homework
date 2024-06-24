package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.CommandExecuteContext;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author ningle
 * @version : WcCommend.java, v 0.1 2024/06/24 11:33 ningle
 **/
public class WcCommend extends InputProcessingCommand {


    public WcCommend(String originCommand) {
        super(originCommand);
    }


    /**
     * wc指令处理，统计文件行数
     *
     * @param context 存储前置指令结果的上下文对象
     * @return 输入文件行数
     */
    @Nonnull
    @Override
    protected Collection<String> commandProcess(CommandExecuteContext context) {
        CharSource charSource = getPendingResourceCharSourceByCommend();
        // 避免无输入
        Preconditions.checkState(null != charSource || null != context.getPreResult(), "wc command has not content");

        try {
            return Collections.singletonList(null == charSource ?
                    context.getPreResult().size() + "" :
                    charSource.lines().count() + "");
        } catch (IOException e) {
            throw new RuntimeException("wc command process error", e);
        }


    }

    /**
     * wc -l filename(可选)
     * 从wc指令中获取文件输入URI
     *
     * @return 待处理源文件URI
     */
    @Nullable
    @Override
    protected String getPendingResourceFileURIByCommend() {
        String[] args = originCommand.split(" ");
        return args.length > 2 ? args[2] : null;
    }

}
