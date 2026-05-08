package com.xunjia.framework.orgPermission.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.OrgPermission;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.vo.GrantOrgVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.org.service.OrganizationService;
import com.xunjia.framework.orgPermission.service.OrgPermissionService;
import com.xunjia.framework.utils.ListUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="有权组织授权记录控制器")
@RestController
@RequestMapping("/orgPermission")
public class OrgPermissionController {

	@Autowired
	private OrgPermissionService service;
	
	@Autowired
	private OrganizationService orgService;

	@ApiOperation(value="跳转至角色授权数据列表页", httpMethod="GET")
	@RequestMapping("/toRoleGrantList")
	@RequiresPermissions("orgPermission:roleGrant")
	public ModelAndView toRoleGrantList() {
		return new ModelAndView("framework/orgPermission/listForRoleGrant");
	}

	@ApiOperation(value="跳转至角色授权选择组织页", httpMethod="GET")
	@RequestMapping("/toGrantOrgForRole")
	@RequiresPermissions("orgPermission:roleGrantSave")
	public ModelAndView toGrantOrgForRole() {
		return new ModelAndView("framework/orgPermission/grantOrgForRole");
	}

	@ApiOperation(value="跳转至用户授权数据列表页", httpMethod="GET")
	@RequestMapping("/toUserGrantList")
	@RequiresPermissions("orgPermission:userGrantSave")
	public ModelAndView toUserGrantList() {
		return new ModelAndView("framework/orgPermission/listForUserGrant");
	}

	@ApiOperation(value="跳转至用户授权选择组织页", httpMethod="GET")
	@RequestMapping("/toGrantOrgForUser")
	public ModelAndView toGrantOrgForUser() {
		return new ModelAndView("framework/orgPermission/grantOrgForUser");
	}

	@ApiOperation(value="查询有权组织分页信息", httpMethod="GET")
	@RequestMapping("/findRightOrgs")
	public PageVO<GrantOrgVO> findRightOrgs(
			@ApiParam(value = "权限所有者类型") String ownerType,
			@ApiParam(value = "权限所有者ID") @RequestParam(defaultValue="0") String ownerId, 
			@ApiParam(value = "当前页号") int page,
			@ApiParam(value = "每页显示条数") int rows) {

		if (ownerId.equals("0") || StringUtils.isEmpty(ownerType)) {
			return new PageVO<GrantOrgVO>();
		}

		PageVO<GrantOrgVO> pageVo = null;
		Page<OrgPermission> authorizedOrgPerms = service.findOrgPermissions(
				ownerType, Integer.parseInt(ownerId), page, rows);
		if (authorizedOrgPerms != null) {
			List<GrantOrgVO> grantVos = new ArrayList<GrantOrgVO>((int)authorizedOrgPerms.getTotalElements());
			for (OrgPermission r : authorizedOrgPerms) {
				r.getOrg().getName(); //lazy load
				String rankName = this.getOrgRankName(r.getOrg());
				GrantOrgVO vo = new GrantOrgVO(r.getOrg(), rankName);
				grantVos.add(vo);
			}
			ListUtils.sort(grantVos, true, "rankName");
			pageVo = new PageVO<GrantOrgVO>(grantVos.size(), grantVos);
		} else {
			pageVo = new PageVO<GrantOrgVO>();
		}
		return pageVo;
	}

	@ApiOperation(value="保存授权信息", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions({ "orgPermission:roleGrantSave", "orgPermission:userGrantSave" })
	public ResponseData<Boolean> save(
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID")String ownerId, 
			@ApiParam(value="组织id数组") @RequestParam(name="orgIds[]",required = false)String[] orgIds){
		List<OrgPermission> records = new ArrayList<OrgPermission>();
		for (int i = 0; i < orgIds.length; i++) {
			Organization org = new Organization();
			org.setId(orgIds[i]);
			OrgPermission r = new OrgPermission();
			r.setOwnerType(ownerType);
			r.setOwnerId(ownerId);
			r.setOrg(org);
			records.add(r);
		}
		return service.saveRecords(records);
	}

	@ApiOperation(value="回收授权信息", httpMethod="POST")
	@RequestMapping("/recover")
	@RequiresPermissions({ "orgPermission:roleGrantSave", "orgPermission:userGrantSave" })
	public ResponseData<Boolean> recover(
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID")String ownerId, 
			@ApiParam(value="组织id数组") @RequestParam(name="orgIds[]",required = false)String[] orgIds){
		ResponseData<Boolean> resp = service.recover(ownerType, ownerId, orgIds);
		return resp;
	}

	@ApiOperation(value="查询有权组织分页信息", httpMethod="GET")
	@RequestMapping("/findOrgs")
	public PageVO<GrantOrgVO> findOrgs(
			@ApiParam(value="组织名称")String name, 
			@ApiParam(value="组织代码")String code, 
			@ApiParam(value="拼音码")String pyCode, 
			@ApiParam(value="上级组织id")String parentId, 
			@ApiParam(value="组织分类id")String typeId,
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID")String ownerId, 
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){

		if (StringUtils.isEmpty(parentId)){
			parentId = "0";
		}

		Page<Organization> pageData = orgService.findOrganizations(name, code, pyCode, parentId, typeId, 1, page, rows);
		PageVO<GrantOrgVO> pageVo = null;
		if (pageData == null) {
			pageVo = new PageVO<GrantOrgVO>();
		} else {
			List<GrantOrgVO> grantOrgVOs = new ArrayList<GrantOrgVO>(pageData.getContent().size());
			List<OrgPermission> rightRecords = service.findRightRecords(ownerType, new String[] { ownerId }, parentId);
			for (Organization o : pageData.getContent()) {
				Optional<OrgPermission> recordOptional = rightRecords.stream().filter(c -> c.getOrg().getId().equals(o.getId())).findFirst();
				GrantOrgVO vo = new GrantOrgVO(o, null);
				if (recordOptional.isPresent()) {
					vo.setOrgRighted(true);
				}
				grantOrgVOs.add(vo);
			}
			pageVo = new PageVO<GrantOrgVO>(pageData.getTotalElements(), grantOrgVOs);
		}
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
