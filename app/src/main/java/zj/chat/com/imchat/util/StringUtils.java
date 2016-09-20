package zj.chat.com.imchat.util;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class StringUtils {
    /**
     * 判断当前字符串是否为无效字符
     *
     * @param str
     * @return
     */
    public static boolean stringIsEmp(String str) {
        if (str == null || "".equals(str) || "null".equals(str) || str.length() < 1) {
            return true;
        }
        return false;
    }


}
