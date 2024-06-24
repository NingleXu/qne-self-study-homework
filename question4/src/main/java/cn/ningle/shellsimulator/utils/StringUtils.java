package cn.ningle.shellsimulator.utils;

/**
 * @author ningle
 * @version : StringUtils.java, v 0.1 2024/06/24 11:49 ningle
 **/
public class StringUtils {
    public static boolean isNotNullAndNotWhitespace(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
