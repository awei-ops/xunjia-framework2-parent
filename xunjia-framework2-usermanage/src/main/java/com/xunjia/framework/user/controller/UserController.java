package com.xunjia.framework.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.Role;
import com.xunjia.framework.usermanage.entity.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.user.service.UserRoleMappingService;
import com.xunjia.framework.user.service.UserService;
import com.xunjia.framework.user.service.UserSettingsService;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="用户信息控制器")
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserSettingsService settingService;
	
	@Autowired
	private UserRoleMappingService urmService;
	
	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;

	@Value("${com.xunjia.framework.security.defaultPassword}")
	private String defaultPassword;

	@Value("${com.xunjia.framework.security.minLength}")
	private String passwordMinLength;

	@Value("${com.xunjia.framework.security.pattern}")
	private String passwordPattern;

	@ApiOperation(value="跳转至添加用户信息页", httpMethod="GET")
	@RequestMapping("/toAdd")
	@RequiresPermissions("user:save")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/user/add");
	}

	@ApiOperation(value="跳转至修改用户信息页", httpMethod="GET")
	@RequestMapping("/toEdit")
	@RequiresPermissions("user:update")
	public ModelAndView toEdit() {
		ModelAndView mav = new ModelAndView("framework/user/edit");
		return mav;
	}

	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	@RequiresPermissions("user:list")
	public ModelAndView toList() {
		return new ModelAndView("framework/user/list");
	}

	@ApiOperation(value="保存用户信息", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions("user:save")
	public ResponseData<Boolean> save(
			@ApiParam(value="用户信息") User user,
			@ApiParam(value="所属组织id")String parentId, 
			@ApiParam(value="所属角色id")String roleIds,
			@ApiParam(value="MultipartRequest对象")MultipartRequest request){
		ResponseData<Boolean> resp = null;
		MultipartFile headImageFile = request.getFile("headImageFile");
		MultipartFile signImageFile = request.getFile("signImageFile");
		if (headImageFile != null && headImageFile.getSize() > 0) {
			String headImagePath = "/headImage/";
			String headImageFileName = FileUtils.copyFile(headImageFile, uploadFolder + headImagePath);
			user.setHeadImage(headImagePath + headImageFileName);
		}
		if (signImageFile != null && signImageFile.getSize() > 0) {
			String signImagePath = "/signImage/";
			String signImageFileName = FileUtils.copyFile(signImageFile, uploadFolder + signImagePath);
			user.setSignImage(signImagePath + signImageFileName);
		}
		
		if (!StringUtils.isEmpty(parentId)) {
			Organization org = new Organization();
			org.setId(parentId);
			user.setOrg(org);
		}
		
		String[] roleIdArray = null;
		if (!StringUtils.isEmpty(roleIds)) {
			roleIdArray = roleIds.split(",");
		}

		resp = userService.save(user, roleIdArray);
		return resp;
	}

	@ApiOperation(value="更新用户信息", httpMethod="POST")
	@RequestMapping("/update")
	@RequiresPermissions("user:update")
	public ResponseData<Boolean> update(
			@ApiParam(value="用户信息")User user, 
			@ApiParam(value="所属组织id")String parentId, 
			@ApiParam(value="所属角色id")String roleIds,
			@ApiParam(value="MultipartRequest对象")MultipartRequest request){
		ResponseData<Boolean> resp = null;
		MultipartFile headImageFile = request.getFile("headImageFile");
		MultipartFile signImageFile = request.getFile("signImageFile");
		if (headImageFile != null && headImageFile.getSize() > 0) {
			String headImagePath = "/headImage/";
			String headImageFileName = FileUtils.copyFile(headImageFile, uploadFolder + headImagePath);
			user.setHeadImage(headImagePath + headImageFileName);
		}
		if (signImageFile != null && signImageFile.getSize() > 0) {
			String signImagePath = "/signImage/";
			String signImageFileName = FileUtils.copyFile(signImageFile, uploadFolder + signImagePath);
			user.setSignImage(signImagePath + signImageFileName);
		}
		
		if (!StringUtils.isEmpty(parentId)) {
			Organization org = new Organization();
			org.setId(parentId);
			user.setOrg(org);
		}
		
		String[] roleIdArray = null;
		if (!StringUtils.isEmpty(roleIds)) {
			roleIdArray = roleIds.split(",");
		}
		
		resp = userService.update(user, roleIdArray);
		return resp;
	}

	@ApiOperation(value="批量删除用户信息", httpMethod="POST")
	@RequestMapping("/delete")
	@RequiresPermissions("user:delete")
	public ResponseData<Boolean> deleteByIds(@ApiParam(value="用户id数组") @RequestParam(name="ids[]")String[] ids){
		return userService.deleteByIds(ids);
	}

	@ApiOperation(value="更新用户可用状态", httpMethod="POST")
	@RequestMapping("/updateEnableState")
	@RequiresPermissions({"user:enable", "user:disable"})
	public ResponseData<Boolean> updateEnableState(@ApiParam(value="可用状态")int enable, 
			@ApiParam(value="用户id数组") @RequestParam(name="ids[]")String[] ids){
		return userService.updateEnableState(enable, ids);
	}

	@ApiOperation(value="根据给定id查询用户信息", httpMethod="GET")
	@RequestMapping("/findById")
	public User findById(@ApiParam(value="用户id")String id) {
		return userService.findById(id);
	}
	
	@ApiOperation(value="根据给定id查询用户信息，同时返回用户所属角色的id", httpMethod="GET")
	@RequestMapping("/findUserWithRoleIdsById")
	public Map<String, Object> findUserWithRoleIdsById(@ApiParam(value="用户id")String id){
		Map<String, Object> map = new HashMap<String, Object>();
		User user = userService.findById(id);
		List<Role> roles = urmService.findRolesByUser(id);
		String roleIds = "";
		if (!ListUtils.isListEmpty(roles)) {
			StringBuffer sb = new StringBuffer();
			for (Role r : roles) {
				sb.append(r.getId()).append(",");
			}
			roleIds = sb.substring(0, sb.length() - 1);
		}
		map.put("user", user);
		map.put("roleIds", roleIds);
		return map;
	}

	@ApiOperation(value="查询用户信息分页数据", httpMethod="GET")
	@RequestMapping("/findUsers")
	@RequiresPermissions("user:list")
	public PageVO<User> findUsers(
			@ApiParam(value="用户名")String username, 
			@ApiParam(value="真实姓名拼音码")String realNamePyCode, 
			@ApiParam(value="可用状态")String enable, 
			@ApiParam(value="所属组织id")String orgId, 
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		if (StringUtils.isEmpty(enable)) {
			enable = "-1";
		}
		if (StringUtils.isEmpty(orgId) && !Context.getCurrentUser().getUsername().equals("admin")){
			orgId = Context.getCurrentUser().getOrg().getId();
		}
		Page<User> pageData = userService.findUsers(username, realNamePyCode, Integer.parseInt(enable), orgId, page, rows);
		PageVO<User> pageVo = new PageVO<User>(pageData);
		return pageVo;
	}

	@ApiOperation(value="保存用户自选主题名称", httpMethod="POST")
	@RequestMapping("/saveCustomTheme")
	public void saveCustomTheme(String theme){
		settingService.saveTheme(theme, Context.getCurrentUser().getId());
	}

	@RequestMapping("/importUsers")
	public ResponseData<Boolean> importUsers(MultipartRequest request){
		MultipartFile file = request.getFile("userFile");
		return userService.importUsers(file);
	}

	@RequestMapping("/findDefaultInfo")
	public Map<String, String> findDefaultInfo(String orgId){
		Integer nextOrderNo = userService.findNextOrderNo(orgId);
		Map<String, String> defaultInfo = new HashMap<>();
		defaultInfo.put("defaultPassword", defaultPassword);
		defaultInfo.put("nextOrderNo", String.valueOf(nextOrderNo));
		return defaultInfo;
	}

	@RequestMapping("/resetPassword")
	@RequiresPermissions("user:resetPassword")
	public ResponseData<Boolean> resetPassword(@RequestParam(name="ids[]") String[] ids){
		return userService.resetPassword(ids);
	}

}
