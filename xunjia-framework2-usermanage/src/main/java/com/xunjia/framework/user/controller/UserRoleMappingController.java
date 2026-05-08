package com.xunjia.framework.user.controller;

import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.Role;
import com.xunjia.framework.usermanage.entity.UserRoleMapping;
import com.xunjia.framework.usermanage.vo.UserOfRoleModel;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.user.service.UserRoleMappingService;
import com.xunjia.framework.utils.StringUtils;

@RestController
@RequestMapping("/userRoleMapping")
public class UserRoleMappingController {

	@Autowired
	private UserRoleMappingService urmService;
	
	@RequestMapping("/toRoleList")
	@RequiresPermissions("userRoleMapping:list")
	public ModelAndView toRoleList() {
		return new ModelAndView("framework/userRoleMapping/roleList");
	}
	
	@RequiresPermissions("urm:manage")
	@RequestMapping("/toSelectUser")
	public ModelAndView toSelectUser() {
		return new ModelAndView("framework/userRoleMapping/selectUser");
	}
	
	@RequestMapping("/saveForRole")
	@RequiresPermissions("urm:manage")
	public ResponseData<Boolean> saveMapping(String roleId, @RequestParam(name="userIds[]")String[] userIds){
		return urmService.save(roleId, userIds);
	}
	
	@RequestMapping("/deleteForRole")
	@RequiresPermissions("urm:manage")
	public ResponseData<Boolean> deleteMappingsForRole(String roleId, @RequestParam(name="userIds[]")String[] userIds){
		return urmService.deleteMappings(roleId, userIds);
	}
	
	@RequestMapping("/findUsersOfRole")
	public PageVO<UserOfRoleModel> findUsersOfRole(String roleId, String username, String realNamePyCode, int page, int rows){
		PageVO<UserOfRoleModel> pageVo = null;
		if (!StringUtils.isEmpty(roleId)) {
			Page<UserRoleMapping> pageData = urmService.findMappings(roleId, username, realNamePyCode, page, rows);
			if (pageData != null) {
				List<UserOfRoleModel> models = new ArrayList<UserOfRoleModel>(pageData.getContent().size());
				for (UserRoleMapping m : pageData.getContent()) {
					String orgRankName = m.getUser().getOrg() != null
							? this.getOrgRankName(m.getUser().getOrg()) : "";
					m.getUser().setOrg(null);
					UserOfRoleModel model = new UserOfRoleModel(m.getUser(), orgRankName);
					models.add(model);
				}
				pageVo = new PageVO<UserOfRoleModel>(pageData.getTotalElements(), models);
			} else {
				pageVo = new PageVO<UserOfRoleModel>(0, new ArrayList<UserOfRoleModel>(0));
			}
		} else {
			pageVo = new PageVO<UserOfRoleModel>(0, new ArrayList<UserOfRoleModel>(0));
		}
		
		return pageVo;
	}
	
	@RequestMapping("/findRolesOfUser")
	public PageVO<Role> findRolesOfUser(String userId){
		List<Role> roleList = urmService.findRolesByUser(userId);
		PageVO<Role> pageVo = new PageVO<Role>(roleList);
		return pageVo;
	}
	
	private String getOrgRankName(Organization currOrg) {
		StringBuffer sb = new StringBuffer();
		sb.append(currOrg.getName());
		if (currOrg.getParent() != null) {
			String parentOrgName = getOrgRankName(currOrg.getParent()) + "/";
			sb.insert(0, parentOrgName);
		}
		return sb.toString();
	}
}
