package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.CommandExecuteContext;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author ningle
 * @version : GrepCommend.java, v 0.1 2024/06/24 11:33 ningle
 **/
public class GrepCommend extends InputProcessingCommand {
    public GrepCommend(String originCommand) {
        super(originCommand);
    }

    /**
     * grep指令处理，留下文件中包含 keyword的行
     *
     * @param context 存储前置指令结果的上下文对象
     * @return grep指令处理结果，包含keyword的行
     */
    @Nonnull
    @Override
    protected Collection<String> commandProcess(CommandExecuteContext context) {
        CharSource charSource = getPendingResourceCharSourceByCommend();
        // 避免无输入
        Preconditions.checkState(null != charSource || null != context.getPreResult(), "grep command has not  content");
        // 获取keyword
        final String keyword = originCommand.split(" ")[1];
        // 输入为空，过滤上条指令结果
        if (null == charSource) {
            return context.getPreResult()
                    .stream().filter(line -> line.contains(keyword))
                    .collect(Collectors.toList());
        }
        // 不为空，过滤文件内容
        Collection<String> collection = new ArrayList<>();
        try {
            charSource.forEachLine(line -> {
                if (line.contains(keyword)) {
                    collection.add(line);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("processing grep command error", e);
        }
        return collection;
    }


    /**
     * grep keyword filename(可选)
     * 从grep指令中获取文件输入URI
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
