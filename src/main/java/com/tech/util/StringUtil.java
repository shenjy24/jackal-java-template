package com.tech.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StringUtil
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-07
 */
public class StringUtil {

    public static List<String> split(String str) {
        return split(str, ",");
    }

    /**
     * 按分隔符拆分字符串，自动去除首尾空白并忽略空串
     *
     * @param str       待拆分字符串
     * @param separator 分隔符（正则）
     * @return 拆分后的非空片段列表
     */
    public static List<String> split(String str, String separator) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(separator))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    public static String join(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return String.join(",", list);
    }

    public static String removeBracketsContent(String str) {
        return str.replaceAll("（.*?）|\\(.*?\\)", "");
    }

    public static String percentEncode(String value) {
        return value != null ? URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~") : null;
    }

    public static String fenToYuan(int fen) {
        BigDecimal yuan = BigDecimal.valueOf(fen)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
        return yuan.toPlainString();
    }

    public static String fenToYuanCompact(int fen) {
        BigDecimal yuan = BigDecimal.valueOf(fen)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);

        String result = yuan.toPlainString();
        if (result.endsWith(".00")) {
            result = result.substring(0, result.length() - 3);
        }
        return result;
    }
}
