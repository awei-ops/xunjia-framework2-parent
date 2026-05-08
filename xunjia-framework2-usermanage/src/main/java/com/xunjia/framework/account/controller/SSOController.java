package com.xunjia.framework.account.controller;

import com.xunjia.framework.account.sso.TokenInfo;
import com.xunjia.framework.account.sso.UserInfo;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.sysSettings.auto.InitSysSettings;
import com.xunjia.framework.user.service.UserService;
import com.xunjia.framework.usermanage.entity.SysSetting;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/unionLoginCallback", method = RequestMethod.GET)
    public void unionLoginCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<SysSetting> sysSettings = InitSysSettings.sysSettings;
        Optional<SysSetting> unionLoginPageUrlOptional = sysSettings.stream()
                .filter(c -> c.getKey().equals("统一认证登录URL")).findFirst();
        Optional<SysSetting> unionLoginClientIdOptional = sysSettings.stream()
                .filter(c -> c.getKey().equals("统一认证客户端ID")).findFirst();
        Optional<SysSetting> unionLoginClientSecretOptional = sysSettings.stream()
                .filter(c -> c.getKey().equals("统一认证客户端秘钥")).findFirst();
        if (!unionLoginPageUrlOptional.isPresent()
                || !unionLoginClientIdOptional.isPresent()
                || !unionLoginClientSecretOptional.isPresent()){
            ssoFail(response, "未正确配置统一认证登录信息。");
            return;
        }

        String accessToken = this.getAccessToken(code, unionLoginPageUrlOptional.get().getValue().trim(),
                unionLoginClientIdOptional.get().getValue().trim(),
                unionLoginClientSecretOptional.get().getValue().trim());
        if (accessToken == null){
            ssoFail(response, "未获取到AccessToken。");
            return;
        }

        UserInfo userInfo = this.getUserInfo(accessToken, unionLoginPageUrlOptional.get().getValue().trim());
        if (userInfo == null){
            ssoFail(response, "未获取到用户信息。");
            return;
        }

        //TODO: 在此处利用返回的userInfo与本系统对照表中的用户信息进行对照，获取本系统用户名，完成自动登录
        String ssoUserName = null;  //利用本系统用户名查询用户信息



        //如果在本系统中查询不到用户信息，自动跳转回登录页
        if (ssoUserName == null){
            ssoFail(response, "未获取到用户信息。");
            return;
        }

        User user = userService.findByUsername(ssoUserName);
        if (user == null){
            ssoFail(response, "用户信息读取失败。");
            return;
        }

        ResponseData<String> resp = userService.userLogin(user.getUsername(), user.getPassword(), true, IpUtils.getIp(request));
    }

    private void ssoFail(HttpServletResponse response, String defaultMsg){
        try {
            response.getWriter().write("<script>window.top.location.href = '/login?msg="+ defaultMsg +"';</script>");
        } catch (IOException e){}
    }

    private String getAccessToken(String code, String ssoUrl, String clientId, String clientSecret){
        String oauthServiceUrl = ssoUrl + "oauth/token";
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<TokenInfo> token = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
        String accessToken = token.getBody().getAccess_token();
        return accessToken;
    }

    private UserInfo getUserInfo(String accessToken, String ssoUrl){
        String getUserInfoUrl = ssoUrl + "users/userinfo?token=" + accessToken;
        ResponseEntity<UserInfo> userInfo = restTemplate.getForEntity(getUserInfoUrl, UserInfo.class);
        return userInfo.getBody();
    }
}