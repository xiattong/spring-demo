package com.xiattong.springframework.utils;

import java.util.Collection;
import java.util.Objects;

/**
 * @author ：xiattong
 * @description：字符串处理工具
 * @version: $
 * @date ：Created in 2021/7/1 17:08
 * @modified By：
 */
public class StringUtils {

    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    public static String toLowerFirstCase(String simpleName) {
        if(Objects.isNull(simpleName)) {
            return null;
        }
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码，大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }


    /**
     * 集合转数组
     * @param collection
     * @return
     */
    public static String[] toStringArray(Collection<String> collection) {
        return collection.toArray(new String[collection.size()]);
    }

    /**
     * empty 判断
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * not empty 判断
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static Object caseStringValue(String value, Class<?> clazz) {
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return value;
        }
        if (clazz == Integer.class) {
            return Integer.valueOf(value);
        }
        if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        }
        return null;
    }

    /**
     * 处理特殊字符
     * @param value
     * @return
     */
    public static String makeStringForRegExp(String value) {
        if (isEmpty(value)) {
            return value;
        }
        return value.replace("\\","\\\\").replace("*","\\*")
            .replace("+","\\+").replace("|","\\|")
            .replace("{","\\{").replace("}","\\}")
            .replace("(","\\(").replace(")","\\)")
            .replace("[","\\[").replace("]","\\]")
            .replace("?","\\?").replace(",","\\,")
            .replace(".","\\.").replace("&","\\&")
            .replace("^","\\^").replace("$","\\$");
    }
}
