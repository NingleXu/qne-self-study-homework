package cn.ningle.shellsimulator.command;

/**
 * @author ningle
 * @version : DirectOutputCommand.java, v 0.1 2024/06/24 14:50 ningle
 **/
public abstract class DirectOutputCommand extends Command {
    public DirectOutputCommand(String originCommand) {
        super(originCommand);
    }
}
