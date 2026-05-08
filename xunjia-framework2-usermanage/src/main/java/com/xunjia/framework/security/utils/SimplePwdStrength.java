package com.xunjia.framework.security.utils;

import com.xunjia.framework.security.exception.PasswordStrengthException;
import com.xunjia.framework.utils.StringUtils;

public class SimplePwdStrength {

    private static final int NUM = 1;
    private static final int SMALL_LETTER = 2;
    private static final int CAPITAL_LETTER = 3;
    private static final int OTHER_CHAR = 4;

    /**
     * 获取密码强度描述信息
     * @param passwordMinLength 密码最小长度
     * @param passwordPattern   密码模式
     * @return
     */
    public static String getPasswordPatternDescr(int passwordMinLength, String passwordPattern){
        StringBuilder sb = new StringBuilder();

        if (passwordMinLength > 0){
            sb.append("新密码长度不可少于").append(passwordMinLength).append("位");
        }

        if (StringUtils.isEmpty(passwordPattern) || passwordPattern.contains("0")){
            if (sb.length() > 0) sb.append("。");
            return sb.toString();
        }

        if (sb.length() > 0){
            sb.append("，且必须包含");
        } else {
            sb.append("新密码中必须包含");
        }

        for (int i = 0; i < passwordPattern.length(); i++){
            char c = passwordPattern.charAt(i);
            if (c == '1'){
                sb.append("数字、");
            } else if (c == '2'){
                sb.append("小写字母、");
            } else if (c == '3'){
                sb.append("大写字母、");
            } else if (c == '4'){
                sb.append("特殊字符、");
            }
        }
        return sb.substring(0, sb.length() - 1) + "。";
    }

    /**
     * 校验密码强度是否符合规范
     * @param pwd 密码
     * @param minLength 最小长度
     * @param pattern 密码模式
     * @return
     * @throws PasswordStrengthException
     */
    public static boolean check(String pwd, int minLength, String pattern) throws PasswordStrengthException {
        if (StringUtils.isEmpty(pwd)){
            throw new PasswordStrengthException("密码不能为空。");
        }
        if (minLength < 0){
            throw new PasswordStrengthException("密码最小长度设置有误，不可小于0。");
        } else {
            //密码长度小于要求的最低长度
            if (pwd.length() < minLength){
                return false;
            }
        }
        if (StringUtils.isEmpty(pattern)){
            throw new PasswordStrengthException("密码模式设置有误，不可为空。");
        }

        if (pattern.contains("0")){
            return true;
        }

        boolean includeNum = false;
        boolean includeSmallLetter = false;
        boolean includeCapitalLetter = false;
        boolean includeOther = false;
        for (int i = 0; i < pwd.length(); i++){
            int type = checkCharacterType(pwd.charAt(i));
            if (type == NUM){
                includeNum = true;
            } else if (type == SMALL_LETTER){
                includeSmallLetter = true;
            } else if (type == CAPITAL_LETTER){
                includeCapitalLetter = true;
            } else {
                includeOther = true;
            }
        }

        String[] patterns = pattern.split(",");
        boolean pwdAllowed = true;
        for (String p : patterns){

            if (!pwdAllowed){
                break;
            }

            switch(p){
                case "1":
                    pwdAllowed = includeNum;
                    break;
                case "2":
                    pwdAllowed = includeSmallLetter;
                    break;
                case "3":
                    pwdAllowed = includeCapitalLetter;
                    break;
                case "4":
                    pwdAllowed = includeOther;
                    break;
            }
        }
        return pwdAllowed;
    }

    private static int checkCharacterType(char c) {
        if (c >= 48 && c <= 57) {
            return NUM;
        }
        if (c >= 65 && c <= 90) {
            return CAPITAL_LETTER;
        }
        if (c >= 97 && c <= 122) {
            return SMALL_LETTER;
        }
        return OTHER_CHAR;
    }
}
