package org.example.wyspring.utils;

import java.util.regex.Pattern;

/**
 * 手机号工具类
 */
public class PhoneUtils {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return true-格式正确
     */
    public static boolean isValid(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 脱敏手机号
     * 13800138000 -> 138****8000
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String mask(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
