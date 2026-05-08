package com.xunjia.framework.account.sso;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfo {

    private String userName;
    private String userId;
    private String loginId;
}
