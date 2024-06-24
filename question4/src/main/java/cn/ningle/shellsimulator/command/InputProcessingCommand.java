package cn.ningle.shellsimulator.command;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author ningle
 * @version : InputProcessingCommand.java, v 0.1 2024/06/24 14:50 ningle
 **/
public abstract class InputProcessingCommand extends Command {

    protected final String inputFileURI;

    public InputProcessingCommand(String originCommand) {
        super(originCommand);
        this.inputFileURI = getPendingResourceFileURIByCommend();
    }


    /**
     * 从命令中获取资源路径并读取
     *
     * @return 获取结果
     */
    @Nullable
    protected CharSource getPendingResourceCharSourceByCommend() {
        String fileURI = getPendingResourceFileURIByCommend();
        if (null == fileURI) return null;

        File file = new File(fileURI);
        Preconditions.checkState(file.exists(), "file {" + fileURI + "} isn't exist!");
        Preconditions.checkState(!file.isDirectory(), "file {" + fileURI + "} is directory!");

        return Files.asCharSource(file, StandardCharsets.UTF_8);
    }

    /**
     * 输入型指令需要从指令中获取源文件URI
     *
     * @return 源文件URI
     */
    @Nullable
    protected abstract String getPendingResourceFileURIByCommend();


}

