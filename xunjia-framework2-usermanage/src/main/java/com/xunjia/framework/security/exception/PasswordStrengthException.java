package com.xunjia.framework.security.exception;

/**
 * 密码强度校验异常
 */
public class PasswordStrengthException extends RuntimeException {

    public PasswordStrengthException(String message){
        super(message);
    }

}
