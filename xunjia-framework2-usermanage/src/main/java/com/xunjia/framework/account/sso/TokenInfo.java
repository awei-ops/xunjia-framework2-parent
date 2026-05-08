package com.xunjia.framework.account.sso;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TokenInfo {

    private String access_token;
    private String token_type;
    private String refresh_token;
    private long expires_in;//过期时间  秒
    private String scope;
    private String jti;
}
