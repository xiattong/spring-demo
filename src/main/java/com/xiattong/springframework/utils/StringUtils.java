package com.xiattong.springframework.utils;

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
}
