package com.polaris.lesscode.app.utils;

/**
 * 安全性工具
 *
 * @author Nico
 * @date 2021/2/26 16:44
 */
public class SecurityUtil {

    public static boolean isSafelyColumn(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
            }else{
                return false;
            }
        }
        return true;
    }
}
