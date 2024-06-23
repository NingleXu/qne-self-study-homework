package cn.ningle.shellsimulator;

/**
 * @author ningle
 * @version : CatCommend.java, v 0.1 2024/06/23 23:54 ningle
 **/
public class CatCommend extends Command {


    public CatCommend(String originCommand, Command nextCommand) {
        super(originCommand, nextCommand);
    }


    @Override
    protected String getPendingResource(String preResult) {
        return "";
    }

    @Override
    protected String commandProcess(String resource) {
        return "";
    }
}
