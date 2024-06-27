package cn.ningle.network.nio.message;

/**
 * @author ningle
 * @version : ResponseMessage.java, v 0.1 2024/06/27 11:18 ningle
 **/
public class ResponseMessage {

    /**
     * 是否出错
     */
    public boolean isError;

    /**
     * 错误原因
     */
    public String errorMessage;

    /**
     * 请求的url
     */
    public String url;

    /**
     * 总字符数
     */
    public long totalCharacterCount;

    /**
     * 英语符号数
     */
    public long englishCharacterCount;

    /**
     * 中文字符数
     */
    public long chineseCharacterCount;

    /**
     * 标点符号数
     */
    public long punctuationMarkCount;


    private ResponseMessage(String url, String errorMessage) {
        this.url = url;
        this.isError = true;
        this.errorMessage = errorMessage;
    }

    private ResponseMessage(String url, long totalCharacterCount, long englishCharacterCount, long chineseCharacterCount, long punctuationMarkCount) {
        this.isError = false;
        this.url = url;
        this.totalCharacterCount = totalCharacterCount;
        this.englishCharacterCount = englishCharacterCount;
        this.chineseCharacterCount = chineseCharacterCount;
        this.punctuationMarkCount = punctuationMarkCount;
    }

    public static ResponseMessage error(String url, String errorMessage) {
        return new ResponseMessage(url, errorMessage);
    }

    public static ResponseMessage success(String url, long totalCharacterCount, long englishCharacterCount, long chineseCharacterCount, long punctuationMarkCount) {
        return new ResponseMessage(url, totalCharacterCount, englishCharacterCount, chineseCharacterCount, punctuationMarkCount);
    }

    @Override
    public String toString() {
        return "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23s |\n", "请求的url", url == null ? "null" : url) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23s |\n", "是否出错", isError) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23s |\n", "错误原因", errorMessage == null ? "null" : errorMessage) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23d |\n", "总字符数", totalCharacterCount) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23d |\n", "英语符号数", englishCharacterCount) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23d |\n", "中文符号数", chineseCharacterCount) +
                "+------------------------+-------------------------+\n" +
                String.format("| %-22s | %-23d |\n", "标点符号数", punctuationMarkCount) +
                "+------------------------+-------------------------+\n";
    }

}
