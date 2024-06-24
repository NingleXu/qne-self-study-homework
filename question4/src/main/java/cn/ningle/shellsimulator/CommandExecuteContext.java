package cn.ningle.shellsimulator;

import java.util.Collection;

/**
 * @author ningle
 * @version : CommandExecuteContext.java, v 0.1 2024/06/24 13:29 ningle
 **/
public class CommandExecuteContext {

    private Collection<String> preResult;

    public Collection<String> getPreResult() {
        return preResult;
    }

    public void setPreResult(Collection<String> preResult) {
        this.preResult = preResult;
    }
}
