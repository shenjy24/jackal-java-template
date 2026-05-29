package com.tech.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * StringUtil
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-07
 */
public class StringUtil {

    public static List<String> split(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        Splitter split = Splitter.on(',').trimResults().omitEmptyStrings();
        return split.splitToList(str);
    }

    public static String join(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return Joiner.on(",").join(list);
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
