package com.xunjia.framework.resource.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.vo.ResourceTreeVO;
import org.apache.shiro.authz.annotation.Logical;
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
import com.xunjia.framework.resource.service.ResourceService;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/resource")
public class ResourceController {

	@Autowired
	private ResourceService service;
	
	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;
	
	@RequestMapping("/toAddMenu")
	@RequiresPermissions("resource:saveMenu")
	public ModelAndView toAddMenu() {
		return new ModelAndView("framework/resource/addMenu");
	}

	@RequestMapping("/toAddBtn")
	@RequiresPermissions("resource:saveBtn")
	public ModelAndView toAddBtn() { return new ModelAndView("framework/resource/addBtn"); }
	
	@RequestMapping("/toEditMenu")
	@RequiresPermissions("resource:update")
	public ModelAndView toEditMenu() {
		return new ModelAndView("framework/resource/editMenu");
	}

	@RequestMapping("/toEditBtn")
	@RequiresPermissions("resource:update")
	public ModelAndView toEditBtn() { return new ModelAndView("framework/resource/editBtn"); }
	
	@RequestMapping("/toList")
	@RequiresPermissions("resource:list")
	public ModelAndView toList() {
		return new ModelAndView("framework/resource/list");
	}
	
	@RequestMapping("/save")
	@RequiresPermissions(value = { "resource:saveMenu", "resource:saveBtn" }, logical = Logical.OR)
	public ResponseData<Boolean> save(Resource resource, String parentId, MultipartRequest request){
		
		MultipartFile imgIconFile = request.getFile("imgIconFile");
		if (imgIconFile != null && !StringUtils.isEmpty(imgIconFile.getName()) && imgIconFile.getSize() > 0) {
			String savePath = "/resource/imgIcon/";
			String fileName = FileUtils.copyFile(imgIconFile, uploadFolder + savePath);
			resource.setImgIcon(savePath + fileName);
		}
		
		if (!StringUtils.isEmpty(parentId) && !"0".equals(parentId)) {
			resource.setParent(new Resource(parentId));
		}
		return service.save(resource);
	}
	
	@RequestMapping("/update")
	@RequiresPermissions("resource:update")
	public ResponseData<Boolean> update(Resource resource, String parentId, MultipartRequest request){
		
		MultipartFile imgIconFile = request.getFile("imgIconFile");
		if (imgIconFile != null && !StringUtils.isEmpty(imgIconFile.getName()) && imgIconFile.getSize() > 0) {
			String savePath = "/resource/imgIcon/";
			String fileName = FileUtils.copyFile(imgIconFile, uploadFolder + savePath);
			resource.setImgIcon(savePath + fileName);
		}
		
		if (!StringUtils.isEmpty(parentId) && !"0".equals(parentId)) {
			resource.setParent(new Resource(parentId));
		}
		return service.update(resource);
	}
	
	@RequestMapping("/delete")
	@RequiresPermissions("resource:delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]")String[] ids){
		return service.delete(ids);
	}
	
	@RequestMapping("/deleteImgIcon")
	public ResponseData<Boolean> deleteImgIcon(String id){
		return service.deleteImgIcon(id);
	}
	
	@RequestMapping("/updateState")
	public ResponseData<Boolean> updateState(int state, @RequestParam(name="ids[]")String[] ids){
		return service.updateState(state, ids);
	}
	
	@RequestMapping("/findResources")
	@RequiresPermissions("resource:list")
	public PageVO<Resource> findResources(String name, String type, String parentId, 
			@RequestParam(defaultValue="-1")String enable, 
			@RequestParam(defaultValue="-1")String allowGrant, 
			int page, int rows) {
		Page<Resource> pageData = service.findResources(name, type, parentId, Integer.parseInt(enable), 
				Integer.parseInt(allowGrant), page, rows);
		PageVO<Resource> pageVo = new PageVO<Resource>(pageData);
		return pageVo;
	}
	
	@RequestMapping("/findById")
	public Resource findById(String id) {
		return service.findById(id);
	}
	
	@RequestMapping("/findAuthorizedMenuTree")
	public List<TreeVO> findAuthorizedMenuTree(){
		List<TreeVO> menuTree = null;
		List<Resource> authorizedResources = Context.getAuthorizedResources();
		if (!ListUtils.isListEmpty(authorizedResources)) {
			List<Resource> authorizedMenus = authorizedResources.stream()
					.filter(c -> c.getType().equals("菜单")).collect(Collectors.toList());
			menuTree = this.buildResourceTree(authorizedMenus, null);
		}
		return menuTree == null ? new ArrayList<TreeVO>(0) : menuTree;
	}
	
	@ApiOperation(value="获取可管理的资源树", httpMethod="GET")
	@RequestMapping("/findResourceTree")
	public List<TreeVO> findResourceTree(){
		List<Resource> authorizedResources = service.findAllResources(); //Context.getAuthorizedResources();
		List<TreeVO> resourceTreeNodes = this.buildResourceTree(authorizedResources, null);
		insertRootNode(resourceTreeNodes);
		return insertRootNode(resourceTreeNodes);
	}
	
	@ApiOperation(value="获取可授权的资源树", httpMethod="GET")
	@RequestMapping("/findGrantResourceTree")
	public List<TreeVO> findGrantResourceTree(){
		List<Resource> grantResources = service.findAllowGrantResources();
		List<TreeVO> resourceTreeNodes = this.buildResourceTree(grantResources, null);
		return insertRootNode(resourceTreeNodes);
	}
	
	private List<TreeVO> buildResourceTree(List<Resource> resources, String parentId){
		List<TreeVO> treeNodes = new LinkedList<TreeVO>();
		List<Resource> subResources = null;
		if (StringUtils.isEmpty(parentId)) {
			subResources = resources.stream().filter(c -> c.getParent() == null).collect(Collectors.toList());
		} else {
			subResources = resources.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(parentId)).collect(Collectors.toList());
		}
		for (Resource resource : subResources) {
			TreeVO treeNode = new ResourceTreeVO(resource);
			treeNode.setChildren(buildResourceTree(resources, resource.getId()));
			if (ListUtils.isListEmpty(treeNode.getChildren())) {
				treeNode.setState(TreeVO.OPEN);
			}
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}
	
	private List<TreeVO> insertRootNode(List<TreeVO> treeNodes) {
		List<TreeVO> rootNodeList = new ArrayList<TreeVO>(0);
		TreeVO node = new TreeVO(0, "系统资源", TreeVO.OPEN, "");
		node.setChildren(treeNodes);
		rootNodeList.add(node);
		return rootNodeList;
	}
}
