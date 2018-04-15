package com.vector.comet.util;

/**
 * Created by jinku on 2018/4/15.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
