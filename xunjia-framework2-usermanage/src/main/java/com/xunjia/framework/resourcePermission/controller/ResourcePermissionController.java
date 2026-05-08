package com.xunjia.framework.resourcePermission.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.ResourcePermission;
import com.xunjia.framework.usermanage.vo.GrantResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.resource.service.ResourceService;
import com.xunjia.framework.resourcePermission.service.ResourcePermissionService;
import com.xunjia.framework.utils.ListUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="资源授权控制器")
@RestController
@RequestMapping("/resPermission")
public class ResourcePermissionController {
	
	@Autowired
	private ResourceService resService;
	
	@Autowired
	private ResourcePermissionService resPermService;

	@RequestMapping("/toRoleGrantList")
	public ModelAndView toRoleGrantList() {
		return new ModelAndView("framework/resPermission/listForRoleGrant");
	}
	
	@RequestMapping("/toUserGrantList")
	public ModelAndView toUserGrantList() {
		return new ModelAndView("framework/resPermission/listForUserGrant");
	}
	
	@ApiOperation(value="跳转至角色授权选择菜单页", httpMethod="GET")
	@RequestMapping("/toGrantResourceForRole")
	public ModelAndView toGrantResourceForRole() {
		return new ModelAndView("framework/resPermission/grantResourceForRole");
	}
	
	@ApiOperation(value="跳转至用户授权选择菜单页", httpMethod="GET")
	@RequestMapping("/toGrantResourceForUser")
	public ModelAndView toGrantResourceForUser() {
		return new ModelAndView("framework/resPermission/grantResourceForUser");
	}
	
	@ApiOperation(value="查询有权菜单分页信息", httpMethod="GET")
	@RequestMapping("/findRightMenus")
	public PageVO<GrantResourceVO> findRightMenus(
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID") String ownerId){
		PageVO<GrantResourceVO> pageVo = null;
		if (StringUtils.isEmpty(ownerId)) {
			pageVo = new PageVO<GrantResourceVO>(0, new ArrayList<GrantResourceVO>(0));
		} else {
			List<ResourcePermission> resPerms = resPermService.findByOwner(ownerType, ownerId);
			if (!ListUtils.isListEmpty(resPerms)) {
				String[] resourceIds = resPerms.stream().map(ResourcePermission::getResourceId).toArray(String[] :: new);
				List<Resource> resources = resService.findByIds(resourceIds);
				List<GrantResourceVO> grantMenuVos = new ArrayList<GrantResourceVO>();
				if (!ListUtils.isListEmpty(resources)) {
					List<Resource> menus = resources.stream().filter(c -> c.getType().equals("菜单")).collect(Collectors.toList());
					for (Resource m : menus) {
						List<Resource> menuSubResources = resources.stream()
								.filter(c -> !c.getType().equals("菜单") 
										&& c.getParent() != null 
										&& c.getParent().getId().equals(m.getId()))
								.collect(Collectors.toList());
						GrantResourceVO grantMenuVo = new GrantResourceVO(m, this.getMenuRankName(m), menuSubResources);
						grantMenuVos.add(grantMenuVo);
					}
				}
				
				ListUtils.sort(grantMenuVos, true, "rankName");
				pageVo = new PageVO<GrantResourceVO>(grantMenuVos.size(), grantMenuVos);
			} else {
				pageVo = new PageVO<GrantResourceVO>(0, new ArrayList<GrantResourceVO>(0));
			}
		}
		return pageVo;
	}
	
	@ApiOperation(value="保存授权信息", httpMethod="POST")
	@RequestMapping("/save")
	public ResponseData<Boolean> save(
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID")String ownerId, 
			@ApiParam(value="资源id数组") @RequestParam(name="resourceIds[]")String[] resourceIds){
		return resPermService.saveResourcePermissions(ownerType, ownerId, resourceIds);
	}
	
	@ApiOperation(value="回收授权信息", httpMethod="POST")
	@RequestMapping("/recover")
	public ResponseData<Boolean> recover(
			@ApiParam(value="权限所有者类型")String ownerType, 
			@ApiParam(value="权限所有者ID")String ownerId, 
			@ApiParam(value="菜单id数组") @RequestParam(name="resourceIds[]")String[] resourceIds){ 
		return resPermService.recoverResourcePermissions(ownerType, ownerId, resourceIds);
	}
	
	@ApiOperation(value="查询有权菜单分页信息", httpMethod="GET")
	@RequestMapping("/findResources")
	public PageVO<GrantResourceVO> findResources(String name, String type, 
			@RequestParam(defaultValue="0")String parentId, 
			String ownerType, String ownerId,
			int page, int rows) {
		Page<Resource> pageData = resService.findResources(name, type, parentId, 1, 1, page, rows);
		PageVO<GrantResourceVO> pageVo = new PageVO<GrantResourceVO>();
		if (pageData != null) {
			List<Resource> resources = pageData.getContent();
			List<ResourcePermission> resPerms = resPermService.findByOwner(ownerType, ownerId);
			List<GrantResourceVO> grantResourceVos = new ArrayList<GrantResourceVO>(resources.size());
			for (Resource r : resources) {
				GrantResourceVO grantResourceVo = new GrantResourceVO(r, "", null);
				Optional<ResourcePermission> optional = resPerms.stream().filter(c ->c .getResourceId().equals(r.getId())).findFirst();
				if (optional.isPresent()) {
					grantResourceVo.setAuthorized(true);
				}
				grantResourceVos.add(grantResourceVo);
			}
			pageVo.setTotal(pageData.getTotalElements());
			pageVo.setRows(grantResourceVos);
		} else {
			pageVo.setRows(new ArrayList<GrantResourceVO>(0));
		}
		return pageVo;
	}
	
	private String getMenuRankName(Resource currMenu) {
		StringBuffer sb = new StringBuffer();
		sb.append(currMenu.getName());
		if (currMenu.getParent() != null) {
			String parentMenuName = getMenuRankName(currMenu.getParent()) + "/";
			sb.insert(0, parentMenuName);
		}
		return sb.toString();
	}
}
