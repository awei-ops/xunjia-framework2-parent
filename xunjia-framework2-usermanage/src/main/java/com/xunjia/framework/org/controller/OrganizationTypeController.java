package com.xunjia.framework.org.controller;

import java.util.List;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.OrganizationType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.org.service.OrganizationTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="组织类型控制器")
@RestController
@RequestMapping("/orgType")
public class OrganizationTypeController {

	@Autowired
	private OrganizationTypeService service;

	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	@RequiresPermissions("orgType:list")
	public ModelAndView toList() {
		return new ModelAndView("framework/orgType/list");
	}
	
	@RequestMapping("/toAdd")
	@RequiresPermissions("orgType:save")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/orgType/add");
	}
	
	@RequestMapping("/toEdit")
	@RequiresPermissions("orgType:update")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/orgType/edit");
	}

	@ApiOperation(value="保存或更新组织分类", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions({ "orgType:save", "orgType:update" })
	public ResponseData<Boolean> saveOrUpdate(@ApiParam(value="组织分类信息") OrganizationType type){
		return service.saveOrUpdate(type);
	}

	@ApiOperation(value="批量删除组织分类", httpMethod="POST")
	@RequestMapping("/delete")
	@RequiresPermissions("orgType:delete")
	public ResponseData<Boolean> deleteByIds(@ApiParam(value="组织分类id数组") @RequestParam(name="ids[]")String[] ids){
		return service.deleteByIds(ids);
	}

	@ApiOperation(value="根据给定id查询组织分类", httpMethod="GET")
	@RequestMapping("/findById")
	public OrganizationType findById(@ApiParam(value="组织分类id") String id) {
		return service.findById(id);
	}

	@ApiOperation(value="获取所有可用的组织分类", httpMethod="GET")
	@RequestMapping("/findAllEnableTypes")
	public List<OrganizationType> findAllEnableTypes(){
		return service.findAllEnableTypes();
	}

	@ApiOperation(value="查询组织分类分页数据", httpMethod="GET")
	@RequestMapping("/findOrganizationTypes")
	@RequiresPermissions("orgType:list")
	public PageVO<OrganizationType> findOrganizationTypes(){
		List<OrganizationType> types = service.findAll();
		PageVO<OrganizationType> pageVo = new PageVO<OrganizationType>(types);
		return pageVo; 
	}
}
