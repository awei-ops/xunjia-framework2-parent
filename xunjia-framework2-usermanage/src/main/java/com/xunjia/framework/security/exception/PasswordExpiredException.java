package com.xunjia.framework.security.exception;

import org.apache.shiro.authc.AccountException;

/**
 * 密码过期异常
 */
public class PasswordExpiredException extends AccountException {

    public PasswordExpiredException(String message){
        super(message);
    }

}
