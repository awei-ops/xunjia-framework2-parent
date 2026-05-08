package com.xunjia.framework.account.controller;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.security.utils.SimplePwdStrength;
import com.xunjia.framework.sysSettings.auto.InitSysSettings;
import com.xunjia.framework.user.service.LoginAuditService;
import com.xunjia.framework.user.service.LoginFailRecordService;
import com.xunjia.framework.user.service.UserService;
import com.xunjia.framework.usermanage.entity.LoginFailRecord;
import com.xunjia.framework.usermanage.entity.SysSetting;
import com.xunjia.framework.utils.IpUtils;
import com.xunjia.framework.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 用户登录/登出控制器
 * 2020年6月9日
 * @author 姜浩
 */
@Api(value = "用户账户登录/登出控制器")
@RestController
public class AccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
	
	@Autowired
	private LoginAuditService loginAuditService;

	@Autowired
	private UserService userService;

	@Autowired
	private LoginFailRecordService failRecordService;

	@Value("${com.xunjia.framework.security.loginFailShowVerifyCode}")
	private int loginFailShowVerifyCode;

	@Value("${com.xunjia.framework.security.pattern}")
	private String passwordPattern;

	@Value("${com.xunjia.framework.security.minLength}")
	private int passwordMinLength;

	@ApiOperation(value="跳转至登录页", httpMethod="GET")
	@RequestMapping({"/", "/login"})
	public ModelAndView login(
			@ApiParam(value="request请求对象") HttpServletRequest request,
			@ApiParam(value="response响应对象") HttpServletResponse response) {
		if (Context.getCurrentUser() == null) {
			if (!StringUtils.isEmpty(request.getHeader("x-requested-with")) &&
					request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
				return null;
			}
			String clientIp = IpUtils.getIp(request);
			ModelAndView mav = new ModelAndView("login");
			List<SysSetting> sysSettings = InitSysSettings.sysSettings;
			Optional<SysSetting> siteNameSetting = sysSettings.stream().filter(c -> c.getKey().equals("站点名称")).findFirst();
			Optional<SysSetting> siteSubNameSetting = sysSettings.stream().filter(c -> c.getKey().equals("站点名称副标题")).findFirst();
			Optional<SysSetting> logoSetting = sysSettings.stream().filter(c -> c.getKey().equals("站点logo")).findFirst();
			Optional<SysSetting> unionLoginPageUrlOptional = sysSettings.stream()
					.filter(c -> c.getKey().equals("统一认证登录URL")).findFirst();
			Optional<SysSetting> unionLoginClientIdOptional = sysSettings.stream()
					.filter(c -> c.getKey().equals("统一认证客户端ID")).findFirst();

			// 默认要显示的信息
			String defaultMsg = request.getParameter("msg");
			mav.addObject("defaultMsg", defaultMsg);

			//是否需要验证码
			LoginFailRecord failRecord = failRecordService.findByIp(clientIp);
			boolean needVerify = false;
			if (failRecord != null && failRecord.getLoginFailCount() >= this.loginFailShowVerifyCode){
				needVerify = true;
			}
			mav.addObject("siteName", siteNameSetting.isPresent() ? siteNameSetting.get().getValue() : "");
			mav.addObject("siteSubName", siteSubNameSetting.isPresent() ? siteSubNameSetting.get().getValue() : "");
			mav.addObject("logo", logoSetting.isPresent() ? logoSetting.get().getValue() : "");
			mav.addObject("needVerify", needVerify);
			mav.addObject("ssoUrl", unionLoginPageUrlOptional.get().getValue().trim());
			mav.addObject("ssoClientId", unionLoginClientIdOptional.get().getValue().trim());
			return mav;
		} else {
			try {
				response.sendRedirect("/index");
			} catch (IOException e) {
				LOGGER.error("AccountController.login方法异常。", e);
			}
			return null;
		}
	}
	
	@ApiOperation(value="提交用户身份验证请求", httpMethod="POST")
	@RequestMapping("/doLogin")
	public ResponseData<String> login(
			@ApiParam(value="用户名",required=true)String username, 
			@ApiParam(value="密码",required=true)String password, 
			@ApiParam(value="request对象，自动注入")HttpServletRequest request){

		String clientIp = IpUtils.getIp(request);
		ResponseData<String> resp = userService.userLogin(username, password, false, clientIp);
		return resp;
	}
	
	@RequestMapping("/unauthorized")
	public ModelAndView toUnauthorized() {
		return new ModelAndView("framework/error/401");
	}

	@RequestMapping("/initUser")
	public ModelAndView initUser(){
		ModelAndView mav = new ModelAndView("changePassword");
		String passwordDescription = SimplePwdStrength.getPasswordPatternDescr(passwordMinLength, passwordPattern);
		mav.addObject("passwordDescription", passwordDescription);
		return mav;
	}

	@RequestMapping("/submitInitUser")
	public ResponseData<Boolean> submitInitUser(String username, String newPassword){
		boolean pwdStrength = SimplePwdStrength.check(newPassword, passwordMinLength, passwordPattern);
		if (!pwdStrength){
			return ResponseData.getFail("密码强度不符合要求。");
		}
		return userService.userInit(username, newPassword);
	}
}
