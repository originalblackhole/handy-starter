package com.github.handy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 17:46
 */
public class StringUtils {

    /**
     * 邮箱格式验证
     */
    public static boolean isValidEmail(String email) {
        String reg = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(\\.[0-9A-Za-z]+)+$";
        Pattern pattern = Pattern.compile(reg);
        boolean flag = false;
        if (email != null) {
            Matcher matcher = pattern.matcher(email);
            flag = matcher.matches();
        }
        return flag;
    }

    /**
     * 输入与正则是否匹配
     */
    public static boolean isMatches(String pattern, String input) {
        Pattern patternName = Pattern.compile(pattern);
        Matcher matcherName = patternName.matcher(input);
        boolean flag = matcherName.matches();
        return flag;
    }

    /*public static void main(String[] args) {
        System.out.println(isValidEmail("shuyang#.lin@zatech.vip.qq.com"));
    }*/
}
