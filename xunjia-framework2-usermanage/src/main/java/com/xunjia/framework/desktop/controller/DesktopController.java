package com.xunjia.framework.desktop.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.resource.vo.HorizontalMenuVO;
import com.xunjia.framework.security.utils.SimplePwdStrength;
import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.SysSetting;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.usermanage.entity.UserSettings;
import com.xunjia.framework.usermanage.vo.ResourceTreeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.resource.service.ResourceService;
import com.xunjia.framework.sysSettings.auto.InitSysSettings;
import com.xunjia.framework.user.service.UserService;
import com.xunjia.framework.utils.Base64Utils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 系统桌面访问控制器
 * 2020年5月8日
 * @author 姜浩
 */
@Api(value = "系统桌面访问控制器")
@RestController
public class DesktopController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);

	/** 用户服务 */
	@Autowired
	private UserService userService;
	
	/** 资源服务 */
	@Autowired
	private ResourceService resourceService;

	@Value("${com.xunjia.framework.security.pattern}")
	private String passwordPattern;

	@Value("${com.xunjia.framework.security.minLength}")
	private int passwordMinLength;

	@ApiOperation(value="跳转至系统索引首页", httpMethod="GET")
	@RequestMapping("/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("framework/desktop/index");
		Map<String, String> sysSettingsMap = initSystemSettings();
		mav.addObject("sysSettings", sysSettingsMap);
		
		List<Resource> authorizedResources = Context.getAuthorizedResources();
		if (!ListUtils.isListEmpty(authorizedResources)) {
			List<Resource> firstLevelMenus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单") && c.getParent() == null)
					.collect(Collectors.toList());
			mav.addObject("firstLevelMenus", firstLevelMenus);
		}

		String passwordStrengthDescription = SimplePwdStrength.getPasswordPatternDescr(passwordMinLength, passwordPattern);
		mav.addObject("passwordStrengthDescription", passwordStrengthDescription);

		//读取用户名称和手机号，生成页面水印
		mav.addObject("userRealName", Context.getCurrentUser().getRealName());
		mav.addObject("userPhone", Context.getCurrentUser().getPhone());

		return mav;
	}

	@RequestMapping("/indexForHorizontalMenu")
	public ModelAndView indexForHorizontalMenu(){
		ModelAndView mav = new ModelAndView("framework/desktop/index2");
		Map<String, String> sysSettingsMap = initSystemSettings();
		mav.addObject("sysSettings", sysSettingsMap);

		String passwordStrengthDescription = SimplePwdStrength.getPasswordPatternDescr(passwordMinLength, passwordPattern);
		mav.addObject("passwordStrengthDescription", passwordStrengthDescription);

		List<Resource> authorizedResources = Context.getAuthorizedResources();
		if (!ListUtils.isListEmpty(authorizedResources)) {
			List<Resource> menus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单"))
					.collect(Collectors.toList());
			List<HorizontalMenuVO> menuVOS = this.buildHorizontalMenuVO(menus, null, 1);
			mav.addObject("menus", menuVOS);
		}
		return mav;
	}

	@RequestMapping("/toHorizontalMenu")
	public ModelAndView toHorizontalMenu(){
		ModelAndView mav = new ModelAndView("framework/desktop/horizontalMenu");
		List<Resource> authorizedResources = Context.getAuthorizedResources();
		if (!ListUtils.isListEmpty(authorizedResources)) {
			List<Resource> menus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单"))
					.collect(Collectors.toList());
			List<HorizontalMenuVO> menuVOS = this.buildHorizontalMenuVO(menus, null, 1);
			mav.addObject("menus", menuVOS);
		}
		return mav;
	}
	
	@ApiOperation(value="获取有权菜单树的节点数据", httpMethod="GET")
	@RequestMapping("/desktop/getMenuTrees")
	public Map<String, List<TreeVO>> getMenuTrees(){
		Map<String, List<TreeVO>> map = new HashMap<String, List<TreeVO>>();
		List<Resource> authorizedResources = Context.getAuthorizedResources();
		if (!ListUtils.isListEmpty(authorizedResources)) {
			List<Resource> firstLevelMenus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单") && c.getParent() == null)
					.collect(Collectors.toList());
			if (!ListUtils.isListEmpty(firstLevelMenus)) {
				for (Resource res : firstLevelMenus) {
					List<TreeVO> treeNodes = this.buildMenuTree(authorizedResources, res.getId());
					map.put(res.getId(), treeNodes);
				}
			}
		}
		return map;
	}
	
	@ApiOperation(value="跳转至默认HOME页", httpMethod="GET")
	@RequestMapping("/desktop/home")
	public ModelAndView home() {
		return new ModelAndView("framework/desktop/home");
	}
	
	@ApiOperation(value="获取当前登录用户的信息", httpMethod="GET")
	@RequestMapping("/desktop/getLoginUser")
	public User getLoginUser() {
		User user = userService.findById(Context.getCurrentUser().getId());
		return user;
	}

	@ApiOperation(value="跳转至首页修改个人信息页面", httpMethod="GET")
	@RequestMapping("/desktop/toEditProfile")
	public ModelAndView toEditProfile() {
		return new ModelAndView("framework/desktop/editProfile");
	}
	
	@ApiOperation(value="修改个人信息", httpMethod="POST")
	@RequestMapping("/desktop/updateProfile")
	public ResponseData<Boolean> updateProfile(
			@ApiParam(value="用户信息")User user,
			@ApiParam(value="默认首页菜单")String defaultMenuId,
			@ApiParam(value="MultipartRequest对象")MultipartRequest request) {
		ResponseData<Boolean> resp = null;
		MultipartFile headImageFile = request.getFile("headImageFile");
		MultipartFile signImageFile = request.getFile("signImageFile");
		try {
			if (headImageFile != null && headImageFile.getSize() > 0) {
				String headImageCode = Base64Utils.fileToBase64ByLocalByte(headImageFile.getBytes());
				user.setHeadImage(headImageCode);
			}
			if (signImageFile != null && signImageFile.getSize() > 0) {
				String signImageCode = Base64Utils.fileToBase64ByLocalByte(signImageFile.getBytes());
				user.setSignImage(signImageCode);
			}
			
			user.setId(Context.getCurrentUser().getId());
			resp = userService.updateProfile(user, defaultMenuId);
			if (resp.isResult()) {
				user = userService.findById(user.getId());
				Context.setCurrentUser(user);
			}
		} catch (Exception e) {
			LOGGER.error("DesktopController.updateProfile方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	@ApiOperation(value="更新用户密码", httpMethod="POST")
	@RequestMapping("/desktop/updatePassword")
	public ResponseData<Boolean> updatePassword(
			@ApiParam(value="原密码",required=true)String oldpassword,
			@ApiParam(value="新密码",required=true)String newpassword){
		boolean pwdStrength = SimplePwdStrength.check(newpassword, passwordMinLength, passwordPattern);
		if (!pwdStrength){
			return ResponseData.getFail("密码强度不符合要求。");
		}
		return userService.updatePassword(oldpassword, newpassword);
	}
	
	/**
	 * 构建有权菜单树
	 * @param authorizedResources 资源集合
	 * @param parentId 上级菜单id
	 * @return 树节点
	 */
	private List<TreeVO> buildMenuTree(List<Resource> authorizedResources, String parentId){
		List<TreeVO> nodes = new LinkedList<TreeVO>();
		List<Resource> subMenus = authorizedResources.stream()
				.filter(c -> c.getType().equals("菜单") && c.getParent() != null && c.getParent().getId().equals(parentId))
				.collect(Collectors.toList());
		if (!ListUtils.isListEmpty(subMenus)) {
			for (Resource res : subMenus) {
				TreeVO node = new ResourceTreeVO(res);
				List<TreeVO> subNodes = buildMenuTree(authorizedResources, res.getId());
				Map<String, Object> attributes = new HashMap<String, Object>();
				attributes.put("url", res.getUrl());
				attributes.put("menuId", res.getId());
				attributes.put("integrateType", res.getIntegrateType());
				node.setChildren(subNodes);
				node.setAttributes(attributes);
				if (ListUtils.isListEmpty(subNodes)) {
					node.setState(TreeVO.OPEN);
				}
				nodes.add(node);
			}
		}
		return nodes;
	}

	private List<HorizontalMenuVO> buildHorizontalMenuVO(List<Resource> authorizedResources, String parentId, int level){
		List<HorizontalMenuVO> menuVOS = new ArrayList<>();
		List<Resource> menus;
		if (StringUtils.isEmpty(parentId)){
			menus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单") && (c.getParent() == null || StringUtils.isEmpty(c.getParent().getId())))
					.collect(Collectors.toList());
		} else {
			menus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单") && c.getParent() != null && parentId.equals(c.getParent().getId()))
					.collect(Collectors.toList());
		}

		if (!ListUtils.isListEmpty(menus)){
			for (Resource menu : menus){
				List<HorizontalMenuVO> subMenus = this.buildHorizontalMenuVO(authorizedResources, menu.getId(), level + 1);
				boolean hasSub = ListUtils.isListEmpty(subMenus) ? false : true;
				HorizontalMenuVO menuVO = new HorizontalMenuVO()
						.setMenu(menu)
						.setHasSub(hasSub)
						.setSubMenus(subMenus)
						.setLevel(level);
				menuVOS.add(menuVO);
			}
		}
		return menuVOS;
	}


	private Map<String, String> initSystemSettings() {
		//初始化参数
		UserSettings userSettings = Context.getCurrentUserSettings();
		List<SysSetting> sysSettings = InitSysSettings.sysSettings;
		Map<String, String> sysSettingsMap = new HashMap<String, String>();
		//页面主题
		if (userSettings != null && !StringUtils.isEmpty(userSettings.getCustomTheme())) {
			sysSettingsMap.put("theme", userSettings.getCustomTheme());
		} else {
			Optional<SysSetting> themeSetting = sysSettings.stream().filter(c -> c.getKey().equals("默认样式名称")).findFirst(); 
			sysSettingsMap.put("theme", themeSetting.isPresent() ? themeSetting.get().getValue() : "superRed");
		}
		//logo图片地址和站点名称
		Optional<SysSetting> logoSetting = sysSettings.stream().filter(c -> c.getKey().equals("站点logo")).findFirst();
		Optional<SysSetting> siteNameSetting = sysSettings.stream().filter(c -> c.getKey().equals("站点名称")).findFirst();
		sysSettingsMap.put("siteLogo", logoSetting.isPresent() ? logoSetting.get().getValue() : "");
		sysSettingsMap.put("siteName", siteNameSetting.isPresent() ? siteNameSetting.get().getValue() : "");
		//默认主页名称和链接地址
		if (userSettings != null && userSettings.getDefaultMenu() != null
				&& !StringUtils.isEmpty(userSettings.getDefaultMenu().getUrl())) {
			Resource defaultMenu = userSettings.getDefaultMenu();
			String menuUrl = defaultMenu.getUrl();
			if (menuUrl.contains("?")) {
				menuUrl += "&menuId=" + defaultMenu.getId();
			} else {
				menuUrl += "?menuId=" + defaultMenu.getId();
			}
			sysSettingsMap.put("defaultMenuName", defaultMenu.getName());
			sysSettingsMap.put("defaultMenuIcon", !StringUtils.isEmpty(defaultMenu.getFontIcon()) ? defaultMenu.getFontIcon() : defaultMenu.getImgIcon());
			sysSettingsMap.put("defaultMenuUrl", menuUrl);
			sysSettingsMap.put("defaultMenuIntegrateType", defaultMenu.getIntegrateType());
		} else {
			//如果当前用户未配置自定义起始菜单，则读取系统默认首页地址
			Optional<SysSetting> defaultPageSetting = sysSettings.stream().filter(c -> c.getKey().equals("默认首页链接")).findFirst();
			sysSettingsMap.put("defaultMenuName", "首页");
			sysSettingsMap.put("defaultMenuIcon", "fa fa-home");
			if (defaultPageSetting.isPresent()) {
				sysSettingsMap.put("defaultMenuUrl", defaultPageSetting.get().getValue());
				Resource sameUrlResource = resourceService.findByUrl(defaultPageSetting.get().getValue());
				String defaultMenuIntegrateType = "iframe";
				if (sameUrlResource != null) {
					defaultMenuIntegrateType = sameUrlResource.getIntegrateType();
				}
				sysSettingsMap.put("defaultMenuIntegrateType", defaultMenuIntegrateType);
			} else {
				sysSettingsMap.put("defaultMenuUrl", "");
			}
		}
		return sysSettingsMap;
	}
}
