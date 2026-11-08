package com.example.lostfound;

/**
 * 统一输入校验工具 —— 移动应用安全加固核心类。
 *
 * 安全目标：
 * 1. 拒绝空值与超长内容，避免脏数据与界面撑爆；
 * 2. 拦截 SQL 注入常见特征字符（单引号、双引号、分号、注释符）；
 * 3. 对异常输入给出统一文案，不把原始输入回显给用户，避免反射型注入。
 */
public final class InputValidator {

    private static final int MAX_TITLE = 40;
    private static final int MAX_DESC = 200;

    private InputValidator() {
    }

    /** 危险字符集合：SQL 注入与脚本注入的常见拼装符号 */
    private static final String[] DANGEROUS = {"'", "\"", ";", "--", "/*", "*/", "<", ">"};

    public static boolean isValidTitle(String s) {
        return isClean(s) && s.length() <= MAX_TITLE;
    }

    public static boolean isValidDesc(String s) {
        return isClean(s) && s.length() <= MAX_DESC;
    }

    private static boolean isClean(String s) {
        if (s == null || s.trim().length() == 0) {
            return false;
        }
        for (String token : DANGEROUS) {
            if (s.contains(token)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 经纬度合法区间校验。
     * 纬度 -90 ~ 90，经度 -180 ~ 180，避免越界坐标导致地图 SDK 异常。
     */
    public static boolean isValidLat(double lat) {
        return lat >= -90 && lat <= 90;
    }

    public static boolean isValidLng(double lng) {
        return lng >= -180 && lng <= 180;
    }

    /** 联系方式脱敏，用于界面展示，避免完整号码暴露 */
    public static String maskContact(String contact) {
        if (contact == null || contact.length() < 7) {
            return "未填写";
        }
        return contact.substring(0, 3) + "****" + contact.substring(contact.length() - 4);
    }
}
