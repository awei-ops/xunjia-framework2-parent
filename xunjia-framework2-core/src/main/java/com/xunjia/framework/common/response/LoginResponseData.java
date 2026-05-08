package com.xunjia.framework.common.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResponseData<T> extends ResponseData<T> {

    public static<T> LoginResponseData<T> get(boolean result, String msg, T data, String url){
        LoginResponseData<T> responseData = new LoginResponseData<>();
        responseData.setHttpCode(HttpCode.OK);
        responseData.setUrl(url);
        responseData.setMsg(msg);
        responseData.setResult(result);
        responseData.setData(data);
        return responseData;
    }

    private String url;

}
