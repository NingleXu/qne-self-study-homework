package cn.ningle.network.nio.message;

/**
 * @author ningle
 * @version : RequestMessage.java, v 0.1 2024/06/27 11:18 ningle
 **/
public class RequestMessage {

    private String requestURL;

    public RequestMessage(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "requestURL='" + requestURL + '\'' +
                '}';
    }
}
