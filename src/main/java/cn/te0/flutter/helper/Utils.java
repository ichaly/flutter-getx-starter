package cn.te0.flutter.helper;

import com.google.common.base.CaseFormat;

/**
 * @author Chaly
 */
public class Utils {
    /**
     * 驼峰转小写下划线
     */
    public static String toLowerUnderline(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    /**
     * 驼峰转大写下划线
     */
    public static String toUpperUnderline(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}