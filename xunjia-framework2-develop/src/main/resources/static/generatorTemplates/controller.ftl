package ${entity.packageName}.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.common.vo.TreeVO;
import ${entity.packageName}.entity.${entity.entityName};
import ${entity.packageName}.service.${entity.entityName}Service;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="${entity.entityDescr}控制器")
@RestController
@RequestMapping("/${entity.entityName?uncap_first}")
public class ${entity.entityName}Controller {
	
	@Autowired
	private ${entity.entityName}Service ${entity.entityName?uncap_first}Service;
	
	<#list operations as operation>
		<#switch operation>
			<#case "添加">
				@ApiOperation(value="跳转至添加页面", httpMethod="GET")
				@RequestMapping("/toAdd")
				@RequiresPermissions("${entity.entityName?uncap_first}:save")
				public ModelAndView toAdd() {
					return new ModelAndView("${entity.entityName?uncap_first}/add");
				}
				
				@ApiOperation(value="保存${entity.entityDescr}信息", httpMethod="POST")
				@RequestMapping("/save")
				@RequiresPermissions("${entity.entityName?uncap_first}:save")
				public ResponseData<Boolean> save(
						@ApiParam(value="${entity.entityDescr}")${entity.entityName} ${entity.entityName?uncap_first}
						<#if entity.treeStructure == 1>
							, @ApiParam(value="上级${entity.entityDescr}id")String parentId
						</#if>){
						
					<#if entity.treeStructure == 1>
						if (!StringUtils.isEmpty(parentId)){
							${entity.entityName} parent${entity.entityName?cap_first} = new ${entity.entityName}();
							parent${entity.entityName?cap_first}.setId(parentId);
							
							${entity.entityName?uncap_first}.setParent(parent${entity.entityName?cap_first});
						}
					</#if>
					return ${entity.entityName?uncap_first}Service.save(${entity.entityName?uncap_first});
				}
				<#break>
			<#case "修改">
				@ApiOperation(value="跳转至添加页面", httpMethod="GET")
				@RequestMapping("/toEdit")
				@RequiresPermissions("${entity.entityName?uncap_first}:update")
				public ModelAndView toEdit() {
					return new ModelAndView("${entity.entityName?uncap_first}/edit");
				}
				
				@ApiOperation(value="更新${entity.entityDescr}信息", httpMethod="POST")
				@RequestMapping("/update")
				@RequiresPermissions("${entity.entityName?uncap_first}:update")
				public ResponseData<Boolean> update(@ApiParam(value="${entity.entityDescr}")${entity.entityName} ${entity.entityName?uncap_first}
						<#if entity.treeStructure == 1>
							, @ApiParam(value="上级${entity.entityDescr}id")String parentId
						</#if>){
					
					<#if entity.treeStructure == 1>
						if (!StringUtils.isEmpty(parentId)){
							${entity.entityName} parent${entity.entityName?cap_first} = new ${entity.entityName}();
							parent${entity.entityName?cap_first}.setId(parentId);
							
							${entity.entityName?uncap_first}.setParent(parent${entity.entityName?cap_first});
						}
					</#if>
					return ${entity.entityName?uncap_first}Service.update(${entity.entityName?uncap_first});
				}
				<#break>
			<#case "删除">
				@ApiOperation(value="批量删除角色信息", httpMethod="POST")
				@RequestMapping("/delete")
				@RequiresPermissions("${entity.entityName?uncap_first}:delete")
				public ResponseData<Boolean> delete(@ApiParam(value="id数组") @RequestParam(name="ids[]")String[] ids){
					return ${entity.entityName?uncap_first}Service.deleteByIds(ids);
				}
				<#break>
			<#case "导入"><#break>
			<#case "导出"><#break>
		</#switch>
	</#list>
	
	<#if enableProp??>
		@ApiOperation(value="修改可用状态", httpMethod="POST")
		@RequestMapping("/updateEnableState")
		@RequiresPermissions({ "${entity.entityName?uncap_first}:enable", "${entity.entityName?uncap_first}:disable" })
		public ResponseData<Boolean> updateEnableState(
				@ApiParam(value="可用状态") ${enableProp.type} enable, 
				@ApiParam(value="id数组") @RequestParam(name="ids[]") String[] ids){
			return ${entity.entityName?uncap_first}Service.updateEnableState(enable, ids);
		}
				
		@ApiOperation(value="查询可用信息", httpMethod="GET")
		@RequestMapping("/findEnable${entity.entityName?cap_first}s")
		@RequiresPermissions("authc")
		public List<${entity.entityName}> findEnable${entity.entityName?cap_first}s(){
			return ${entity.entityName?uncap_first}Service.findEnable${entity.entityName?cap_first}s();
		}
	</#if>
	
	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	@RequiresPermissions("${entity.entityName?uncap_first}:list")
	public ModelAndView toList() {
		return new ModelAndView("${entity.entityName?uncap_first}/list");
	}

	@ApiOperation(value="根据给定id查询${entity.entityDescr}信息", httpMethod="GET")
	@RequestMapping("/findById")
	@RequiresPermissions("authc")
	public ${entity.entityName} findById(@ApiParam(value="id")String id) {
		return ${entity.entityName?uncap_first}Service.findById(id);
	}
	
	@ApiOperation(value="查询${entity.entityDescr}信息分页数据", httpMethod="GET")
	@RequestMapping("/find${entity.entityName?cap_first}s")
	@RequiresPermissions("${entity.entityName?uncap_first}:list")
	public PageVO<${entity.entityName}> find${entity.entityName?cap_first}s(
		<#list entity.properties as p>
			<#if p.enableFlag == 0 && p.searchFlag == 1>
				<#if p.searchCond == "between">
					<#if p.type == "Date">
						@ApiParam(value="起始${p.propDescr}") String ${p.propName}Start, @ApiParam(value="截止${p.propDescr}") String ${p.propName}End,
					<#else>
						@ApiParam(value="起始${p.propDescr}") ${p.type} ${p.propName}Start, @ApiParam(value="截止${p.propDescr}") ${p.type} ${p.propName}End,
					</#if>
				<#else>
					<#if p.type == "Date">
						@ApiParam(value="${p.propDescr}") String ${p.propName},
					<#else>
						@ApiParam(value="${p.propDescr}") ${p.type} ${p.propName},
					</#if>
				</#if>
			<#elseif p.enableFlag == 1 && p.searchFlag == 1>
			    Integer enableState,
			</#if>
		</#list>
		<#if entity.treeStructure == 1>
			String parentId, 
		</#if>
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
			
		<#list entity.properties as p>
			<#if p.searchFlag == 1 && p.dicFlag == 1>
				if (!StringUtils.isEmpty(${p.propName}) && ${p.propName}.equals("请选择")){
					${p.propName} = null;
				}
			</#if>
		</#list>
		
		Page<${entity.entityName}> pageData = ${entity.entityName?uncap_first}Service.find${entity.entityName?cap_first}s(
			<#list entity.properties as p>
				<#if p.enableFlag == 0 && p.searchFlag == 1>
					<#if p.searchCond == "between">
						${p.propName}Start, ${p.propName}End, 
					<#else>
						${p.propName}, 
					</#if>
				<#elseif p.enableFlag == 1 && p.searchFlag == 1>
				    enableState,
				</#if>
			</#list>
			<#if entity.treeStructure == 1>
				parentId, 
			</#if>
			page, rows);
		PageVO<${entity.entityName}> pageVo = new PageVO<${entity.entityName}>(pageData);
		return pageVo;
	}
	
	<#if entity.treeStructure == 1>
		@ApiOperation(value="获取${entity.entityDescr}的树", httpMethod="GET")
		@RequestMapping("/get${entity.entityName?cap_first}Tree")
		public List<TreeVO> get${entity.entityName?cap_first}Tree(@ApiParam(value="上级${entity.entityDescr}id") String id) {
			
			List<${entity.entityName}> itemList = ${entity.entityName?uncap_first}Service.findByParent(id);
			List<TreeVO> treeNodes = new LinkedList<TreeVO>();
			if (!ListUtils.isListEmpty(itemList)){
				for (${entity.entityName} item : itemList){
					TreeVO treeNode = new TreeVO(item.getId(), item.getName(), TreeVO.CLOSED, null);
					treeNodes.add(treeNode);
				}
			}
			
			if (StringUtils.isEmpty(id)){
				List<TreeVO> rootNodes = new ArrayList<TreeVO>(0);
				TreeVO root = new TreeVO("", "${entity.entityDescr}", TreeVO.OPEN, null);
				root.setChildren(treeNodes);
				rootNodes.add(root);
				return rootNodes;
			} else {
				return treeNodes;
			}
		}

		<#if enableFlag == 1>
		    @ApiOperation(value="获取可用的${entity.entityDescr}树", httpMethod="GET")
            @RequestMapping("/getEnable${entity.entityName?cap_first}Tree")
            public List<TreeVO> getEnable${entity.entityName?cap_first}Tree(@ApiParam(value="上级${entity.entityDescr}id") String id) {

            	List<${entity.entityName}> itemList = ${entity.entityName?uncap_first}Service.findByParent(id, 1);
            	List<TreeVO> treeNodes = new LinkedList<TreeVO>();
            	if (!ListUtils.isListEmpty(itemList)){
            		for (${entity.entityName} item : itemList){
            			TreeVO treeNode = new TreeVO(item.getId(), item.getName(), TreeVO.CLOSED, null);
            			treeNodes.add(treeNode);
            		}
            	}

            	if (StringUtils.isEmpty(id)){
            		List<TreeVO> rootNodes = new ArrayList<TreeVO>(0);
            		TreeVO root = new TreeVO("", "${entity.entityDescr}", TreeVO.OPEN, null);
            		root.setChildren(treeNodes);
            		rootNodes.add(root);
            		return rootNodes;
            	} else {
            		return treeNodes;
            	}
            }
		</#if>
	</#if>

}