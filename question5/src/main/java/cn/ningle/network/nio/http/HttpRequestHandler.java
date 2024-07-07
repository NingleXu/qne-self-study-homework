package cn.ningle.network.nio.http;

import cn.ningle.network.nio.message.RequestMessage;
import cn.ningle.network.nio.message.ResponseMessage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ningle
 * @version : HttpRequestHandler.java, v 0.1 2024/06/27 12:48 ningle
 **/
public class HttpRequestHandler {

    private static final int TIMEOUT_MS = 1000; // 设置超时时间为 1 秒


    public static final ThreadPoolExecutor REQUEST_EXECUTORS = new ThreadPoolExecutor(
            10,
            20,
            1000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static ResponseMessage doRequest(RequestMessage requestMessage) {
        String requestURL = requestMessage.getRequestURL();

        if (!isValidURL(requestURL)) {
            return ResponseMessage.error(requestURL, "Invalid request URL:" + requestURL);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(requestURL);

            // 设置请求配置，包括超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT_MS)
                    .setConnectTimeout(TIMEOUT_MS)
                    .setConnectionRequestTimeout(TIMEOUT_MS)
                    .build();
            request.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return ResponseMessage.error(requestURL, "Response is null");
            }
            String responseString = EntityUtils.toString(entity);

            // 统计字符数、汉字数、英文字符数、标点符号数
            long totalCharacterCount = responseString.length();
            long chineseCharacterCount = countChineseCharacters(responseString);
            long englishCharacterCount = countEnglishCharacters(responseString);
            long punctuationMarkCount = totalCharacterCount - chineseCharacterCount - englishCharacterCount;

            return ResponseMessage.success(requestURL, totalCharacterCount, englishCharacterCount, chineseCharacterCount, punctuationMarkCount);
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            return ResponseMessage.error(requestURL, "server request timed out,reason: " + e.getMessage());
        } catch (Exception e) {
            return ResponseMessage.error(requestURL, "unknown request error,reason: " + e.getMessage());
        }
    }

    private static long countChineseCharacters(String text) {
        long count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isChineseCharacter(c)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isChineseCharacter(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    private static long countEnglishCharacters(String text) {
        long count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                count++;
            }
        }
        return count;
    }

    private static long countPunctuationMarks(String text) {
        long count = 0;
        String punctuationMarks = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (punctuationMarks.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }


    public static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            // 如果没有抛出异常，则说明 URL 格式正确
            return true;
        } catch (MalformedURLException e) {
            // URL 格式错误
            return false;
        }
    }

}
