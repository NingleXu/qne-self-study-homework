package cn.ningle.shellsimulator.command;

import cn.ningle.shellsimulator.CommandExecuteContext;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ningle
 * @version : LsCommand.java, v 0.1 2024/06/24 22:41 ningle
 **/
public class LsCommand extends DirectOutputCommand {

    public LsCommand(String originCommand) {
        super(originCommand);
    }

    /**
     * 执行ls命令处理
     *
     * @param context 存储前置指令结果的上下文对象
     * @return ls目标URI目录下文件和文件夹名称
     */
    @Nonnull
    @Override
    protected Collection<String> commandProcess(CommandExecuteContext context) {
        // 从命令中读取URI
        String[] args = originCommand.split(" ");
        String uri = args.length == 1 ? "." : args[1];

        Path path = Paths.get(uri);
        Preconditions.checkArgument(Files.isDirectory(path), uri + " is not a directory.");

        Collection<String> collection = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            stream.forEach(p -> collection.add(p.getFileName().toString()));
        } catch (IOException e) {
            throw new RuntimeException("ls command process error!", e);
        }


        return collection;
    }
}
