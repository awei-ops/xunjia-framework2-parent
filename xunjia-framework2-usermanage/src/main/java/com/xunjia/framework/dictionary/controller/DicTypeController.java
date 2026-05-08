package com.xunjia.framework.dictionary.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.DicType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.dictionary.service.DicTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 字典分类控制器
 * 2020年5月8日
 * @author 姜浩
 */
@Api(value="字典分类控制器")
@RestController
@RequestMapping("/dicType")
public class DicTypeController {
	
	@Autowired
	private DicTypeService typeService;

	@ApiOperation(value="跳转至添加字典分类页", httpMethod="GET")
	@RequestMapping("/toAdd")
	@RequiresPermissions("dicType:save")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/dic/addDicType");
	}
	
	@ApiOperation(value="跳转至编辑字典分类页", httpMethod="GET")
	@RequestMapping("/toEdit")
	@RequiresPermissions("dicType:update")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/dic/editDicType");
	}
	
	@ApiOperation(value="跳转至字典管理页面", httpMethod="GET")
	@RequestMapping("/toList")
	@RequiresPermissions("dic:list")
	public ModelAndView toList() {
		return new ModelAndView("framework/dic/dicList");
	}
	
	@ApiOperation(value="保存字典分类", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions("dicType:save")
	public ResponseData<Boolean> save(@ApiParam(value="字典分类实体对象")DicType type){
		return typeService.save(type);
	}

	@ApiOperation(value="更新字典分类", httpMethod="POST")
	@RequestMapping("/update")
	@RequiresPermissions("dicType:update")
	public ResponseData<Boolean> update(@ApiParam(value="字典分类实体对象") DicType type){
		return typeService.update(type);
	}

	@ApiOperation(value="批量删除字典分类", httpMethod="POST")
	@RequestMapping("/delete")
	@RequiresPermissions("dicType:delete")
	public ResponseData<Boolean> delete(
			@ApiParam(value="字典分类id数组") @RequestParam(name="ids[]")String[] ids){
		return typeService.deleteByIds(ids);
	}
	
	@RequestMapping("/importDicTypes")
	@RequiresPermissions("dicType:import")
	public ResponseData<Boolean> importDicTypes(MultipartRequest request){
		MultipartFile file = request.getFile("dicTypeFile");
		return typeService.importTypes(file);
	}

	@ApiOperation(value="根据id查询字典分类信息", httpMethod="GET")
	@RequestMapping("/findById")
	public DicType findById(@ApiParam(value="字典分类id")String id) {
		return typeService.findById(id);
	}
	

	@ApiOperation(value="查询字典分类分页数据", httpMethod="GET")
	@RequestMapping("/findDicTypes")
	@RequiresPermissions("dic:list")
	public PageVO<DicType> findDicTypes(
			@ApiParam(value="字典分类名称")String name, 
			@ApiParam(value="字典分类编码")String code, 
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		Page<DicType> pageData = typeService.findDicTypes(name, code, page, rows);
		PageVO<DicType> pageVo = new PageVO<DicType>(pageData);
		return pageVo;
	}
}
