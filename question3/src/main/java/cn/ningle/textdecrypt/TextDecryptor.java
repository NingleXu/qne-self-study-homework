package cn.ningle.textdecrypt;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ningle
 * @version : TextDecryptor.java, v 0.1 2024/06/22 22:33 ningle
 * 依据规则的文本解密器
 * 1) natureOrder 自然排序，即文本中排列顺序
 * 2) indexOrder 索引排序，文本中每行第一个数字为索引
 * 3) charOrder 文本排序，java 的字符排序
 * 4) charOrderDESC 文本倒序，java 的字符倒序
 **/
public class TextDecryptor {

    /**
     * 解密数据文本路径
     */
    private final String propTextPath;

    /**
     * 被加密文本路径
     */
    private final String encryptedTextPath;

    /**
     * 解密后文本存放路径
     */
    private final String decryptedTextPath;


    private static final String FUNCTION_REGEX = "\\$(indexOrder|charOrder|charOrderDESC|natureOrder)(\\([^()]*\\))";

    /**
     * 解密数据的自然排序map集合
     */
    private List<String> propNatureOrderList;

    private List<String> propNatureOrderAscOrderList;

    private List<String> propNatureOrderDescOrderList;

    /**
     * 解密数据的索引排序map集合
     */
    private final HashMap<Integer, String> propIdxOrderMap = new HashMap<>();


    public void doDecrypt() {
        // 预加载与处理解密数据
        propDataPreload();

        // 校验并获取 待解密数据 CharSource
        CharSource charSource = checkEncryptedFileAndGetCharSource();

        // 校验并获取 解密后数据文件 CharSink
        CharSink charSink = ensureEncryptedFileAbsendAndGetCharSink();

        // 逐行解密
        try {
            try (Writer writer = charSink.openBufferedStream()) {
                charSource.lines().forEach(line -> {
                    try {
                        // 写入解密结果
                        writer.write(handleSingleLine(line));
                        // 写入换行符
                        writer.write(System.lineSeparator());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException("error while decrypting", e);
        }
    }

    /**
     * 单行解密
     *
     * @param line 待解密文本中的某一行
     * @return 解密结果
     */
    private String handleSingleLine(String line) {

        Pattern pattern = Pattern.compile(FUNCTION_REGEX);
        Matcher matcher = pattern.matcher(line);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String functionName = matcher.group(1);
            // 去除括号
            String index = matcher.group(2).replace("(", "").replace(")", "");
            // 执行解密
            String decryptVal = DecrytorFunctionEnum.doHandle(functionName, this, Integer.parseInt(index));
            // 对满足正则表达式的内容执行替换
            matcher.appendReplacement(result, decryptVal);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 删除如果解密文件已存在，并返回CharSink
     *
     * @return CharSink
     */
    private CharSink ensureEncryptedFileAbsendAndGetCharSink() {
        File file = new File(decryptedTextPath + "/sdxl.txt");
        file.deleteOnExit();
        return Files.asCharSink(new File(decryptedTextPath + "/sdxl.txt"), StandardCharsets.UTF_8);
    }

    /**
     * 校验文件路径并返回 字节源
     *
     * @return 文件字节源
     */
    private CharSource checkEncryptedFileAndGetCharSource() {
        File file = new File(encryptedTextPath);
        // 校验文件是否存在
        Preconditions.checkArgument(file.exists(), "File does not exist: " + encryptedTextPath);
        // 校验是否为文件夹
        Preconditions.checkArgument(file.isFile(), "File is not a file: " + encryptedTextPath);

        return Files.asCharSource(file, StandardCharsets.UTF_8);
    }

    /**
     * 预加载解密数据
     */
    private void propDataPreload() {
        File file = new File(propTextPath);
        // 校验文件是否存在
        Preconditions.checkArgument(file.exists(), "File does not exist");
        // 校验不能是文件夹
        Preconditions.checkArgument(file.isFile(), "File is not a file");

        try {
            // 自然排序
            propNatureOrderList = Files.asCharSource(file, StandardCharsets.UTF_8)
                    .lines()
                    .map(line -> {
                        int firstBlankIdx = line.indexOf("\t");

                        Preconditions.checkArgument(firstBlankIdx != -1, "Blank idx can't found in line");

                        Integer propIdxOrder = Integer.parseInt(line.substring(0, firstBlankIdx));
                        // 将索引排序加入map中
                        String propVal = line.substring(firstBlankIdx + 1);
                        propIdxOrderMap.put(propIdxOrder, propVal);
                        return propVal;
                    })
                    .collect(Collectors.toList());

            // 字符串正序
            propNatureOrderAscOrderList = new ArrayList<>(propNatureOrderList);
            propNatureOrderAscOrderList.sort(String::compareTo);
            // 字符串倒序
            propNatureOrderDescOrderList = new ArrayList<>(propNatureOrderAscOrderList);
            Collections.reverse(propNatureOrderDescOrderList);

        } catch (Exception e) {
            throw new RuntimeException("Error while reading file " + file.getAbsolutePath(), e);
        }
    }

    public TextDecryptor(String propTextPath, String encryptedTextPath, String decryptedTextPath) {
        this.propTextPath = propTextPath;
        this.decryptedTextPath = decryptedTextPath;
        this.encryptedTextPath = encryptedTextPath;
    }


    /**
     * @author ningle
     * @version : DecrytorFunctionEnum.java, v 0.1 2024/06/22 23:49 ningle
     * 自定义函数处理枚举类
     **/
    private enum DecrytorFunctionEnum implements BiFunction<TextDecryptor, Integer, String> {
        // 自然排序
        NATURE_ORDER("natureOrder") {
            @Override
            public String apply(TextDecryptor textDecryptor, Integer index) {
                return textDecryptor.propNatureOrderList.get(index);
            }
        },
        // 索引排序
        INDEX_ORDER("indexOrder") {
            @Override
            public String apply(TextDecryptor textDecryptor, Integer index) {
                return textDecryptor.propIdxOrderMap.get(index);
            }
        },
        // 字符串顺序
        CHAR_ORDER("charOrder") {
            @Override
            public String apply(TextDecryptor textDecryptor, Integer index) {
                return textDecryptor.propNatureOrderAscOrderList.get(index);
            }
        },
        // 字符串倒序
        CHAR_ORDER_DESC("charOrderDESC") {
            @Override
            public String apply(TextDecryptor textDecryptor, Integer index) {
                return textDecryptor.propNatureOrderDescOrderList.get(index);
            }
        };

        private final String functionName;

        private static final Map<String, DecrytorFunctionEnum> DECRYTOR_FUNCTION_ENUM_HASH_MAP =
                Arrays.stream(values())
                        .collect(Collectors.toMap(e -> e.functionName, Function.identity()));


        public static String doHandle(String functionName, TextDecryptor textDecryptor, Integer index) {
            if (!DECRYTOR_FUNCTION_ENUM_HASH_MAP.containsKey(functionName)) {
                throw new IllegalArgumentException("Unknown function: " + functionName);
            }
            return DECRYTOR_FUNCTION_ENUM_HASH_MAP.get(functionName).apply(textDecryptor, index);
        }

        DecrytorFunctionEnum(String functionName) {
            this.functionName = functionName;
        }
    }
}


