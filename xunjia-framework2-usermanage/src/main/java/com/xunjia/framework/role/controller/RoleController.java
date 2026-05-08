package com.xunjia.framework.role.controller;

import java.util.List;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.Role;
import com.xunjia.framework.usermanage.entity.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.role.service.RoleService;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="系统角色控制器")
@RestController
@RequestMapping("/role")
public class RoleController {

	@Autowired
	private RoleService service;

	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	@RequiresPermissions("role:list")
	public ModelAndView toList() {
		return new ModelAndView("framework/role/list");
	}
	
	@RequestMapping("/toAdd")
	@RequiresPermissions("role:save")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/role/add");
	}
	
	@RequestMapping("/toEdit")
	@RequiresPermissions("role:update")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/role/edit");
	}

	@ApiOperation(value="保存角色信息", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions("role:save")
	public ResponseData<Boolean> save(@ApiParam(value="角色信息") Role role){
		return service.save(role);
	}

	@ApiOperation(value="更新角色信息", httpMethod="POST")
	@RequestMapping("/update")
	@RequiresPermissions("role:update")
	public ResponseData<Boolean> update(@ApiParam(value="角色信息")Role role){
		return service.update(role);
	}

	@ApiOperation(value="批量删除角色信息", httpMethod="POST")
	@RequestMapping("/delete")
	@RequiresPermissions("role:delete")
	public ResponseData<Boolean> delete(@ApiParam(value="角色id数组") @RequestParam(name="ids[]")String[] ids){
		return service.deleteByIds(ids);
	}

	@ApiOperation(value="修改角色可用状态", httpMethod="POST")
	@RequestMapping("/updateEnableState")
	@RequiresPermissions({ "role:enable", "role:disable" })
	public ResponseData<Boolean> updateEnableState(
			@ApiParam(value="可用状态") int enable, 
			@ApiParam(value="角色id数组") @RequestParam(name="ids[]") String[] ids){
		return service.updateEnableState(enable, ids);
	}

	@ApiOperation(value="根据给定id查询角色信息", httpMethod="GET")
	@RequestMapping("/findById")
	@RequiresPermissions("authc")
	public Role findById(@ApiParam(value="角色id")String id) {
		return service.findById(id);
	}

	@ApiOperation(value="查询可用角色信息", httpMethod="GET")
	@RequestMapping("/findEnableRoles")
	@RequiresPermissions("user:save")
	public List<Role> findEnableRoles(String orgId){
		return service.findEnableRoles(orgId);
	}
	
	@ApiOperation(value="查询角色信息分页数据", httpMethod="GET")
	@RequestMapping("/findRoles")
	@RequiresPermissions("role:list")
	public PageVO<Role> findRoles(
			@ApiParam(value="角色名称")String name,
			@ApiParam(value="拼音码")String pyCode, 
			@ApiParam(value="可用状态")String enable,
			@ApiParam(value="组织id")String orgId,
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		if (StringUtils.isEmpty(enable)) {
			enable = "-1";
		}
		if (StringUtils.isEmpty(orgId)){
			User currentUser = Context.getCurrentUser();
			if (!currentUser.getUsername().equals("admin")){
				orgId = currentUser.getOrg().getId();
			}
		}
		Page<Role> pageData = service.findRoles(name, pyCode, Integer.parseInt(enable), orgId, page, rows);
		PageVO<Role> pageVo = new PageVO<>(pageData);
		return pageVo;
	}
}
