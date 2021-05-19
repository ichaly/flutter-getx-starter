package cn.te0.flutter.utils;

import com.google.common.base.CaseFormat;

/**
 * @author Chaly
 */
public class Utils {
    /**
     * 驼峰转下划线
     */
    public static String toUnderline(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }
}
