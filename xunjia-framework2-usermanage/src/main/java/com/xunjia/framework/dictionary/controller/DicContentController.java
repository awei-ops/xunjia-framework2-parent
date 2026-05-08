package com.xunjia.framework.dictionary.controller;

import java.util.List;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.DicContent;
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
import com.xunjia.framework.dictionary.service.DicContentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 字典内容控制器
 * 2020年5月8日
 * @author 姜浩
 */
@Api(value="字典内容控制器")
@RestController
@RequestMapping("/dicContent")
public class DicContentController {

	@Autowired
	private DicContentService dcService;
	
	@ApiOperation(value="跳转至添加字典内容页", httpMethod="GET")
	@RequestMapping("/toAdd")
	@RequiresPermissions("dicContent:save")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/dic/addDicContent");
	}

	@ApiOperation(value="跳转至编辑字典内容页", httpMethod="GET")
	@RequestMapping("/toEdit")
	@RequiresPermissions("dicContent:update")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/dic/editDicContent");
	}

	@ApiOperation(value="保存字典内容", httpMethod="POST")
	@RequestMapping("/save")
	@RequiresPermissions("dicContent:save")
	public ResponseData<Boolean> save(@ApiParam(value="字典内容实体对象") DicContent dc){
		return dcService.save(dc);
	}

	@ApiOperation(value="更新字典内容", httpMethod="POST")
	@RequestMapping("/update")	
	@RequiresPermissions("dicContent:update")
	public ResponseData<Boolean> update(@ApiParam(value="字典内容实体对象")DicContent dc){
		return dcService.update(dc);
	}

	@ApiOperation(value="批量删除字典内容", httpMethod="POST")
	@RequestMapping("/delete")
	@RequiresPermissions("dicContent:delete")
	public ResponseData<Boolean> delete(@ApiParam(value="字典内容id数组") @RequestParam(name="ids[]")String[] ids){
		return dcService.delete(ids);
	}
	
	@RequestMapping("/importDicContents")
	@RequiresPermissions("dicContent:import")
	public ResponseData<Boolean> importDicContents(MultipartRequest request){
		MultipartFile file = request.getFile("dicContentFile");
		return dcService.importContents(file);
	}

	@ApiOperation(value="根据id查询字典内容", httpMethod="GET")
	@RequestMapping("/findById")
	public DicContent findById(@ApiParam(value="字典内容id")String id) {
		return dcService.findById(id);
	}

	@ApiOperation(value="查询字典内容分页数据", httpMethod="GET")
	@RequestMapping("/findDicContents")
	@RequiresPermissions("dic:list")
	public PageVO<DicContent> findDicContents(
			@ApiParam(value="字典内容名称")String name, 
			@ApiParam(value="字典内容编码")String code, 
			@ApiParam(value="字典分类id")String typeId, 
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		Page<DicContent> pageData = dcService.findDicContents(name, code, typeId, page, rows);
		PageVO<DicContent> pageVo = new PageVO<DicContent>(pageData);
		return pageVo;
	}

	@RequestMapping("/findByTypeCode")
	public List<DicContent> findByTypeCode(String typeCode){
		return dcService.findByTypeCode(typeCode);
	}
	
	@RequestMapping("/findByTypeCodeWithBlank")
	public List<DicContent> findByTypeCodeWithBlank(String typeCode){
		List<DicContent> contents = dcService.findByTypeCode(typeCode);
		DicContent blank = new DicContent();
		blank.setId("");
		blank.setName("请选择");
		blank.setCode("");
		contents.add(0, blank);
		return contents;
	}

	@RequestMapping("/findByTypeCodeWithAll")
	public List<DicContent> findByTypeCodeWithAll(String typeCode){
		List<DicContent> contents = dcService.findByTypeCode(typeCode);
		DicContent blank = new DicContent();
		blank.setId("");
		blank.setName("全部");
		blank.setCode("");
		contents.add(0, blank);
		return contents;
	}
}
