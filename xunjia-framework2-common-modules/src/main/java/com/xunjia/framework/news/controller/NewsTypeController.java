package com.xunjia.framework.news.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.news.entity.NewsType;
import com.xunjia.framework.news.service.NewsTypeService;
import com.xunjia.framework.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.common.vo.TreeVO;

@RestController
@RequestMapping("/newsType")
public class NewsTypeController {

	@Autowired
	private NewsTypeService service;
	
	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("news/newsType/add");
	}
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit() {
		return new ModelAndView("news/newsType/edit");
	}
	
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("news/newsType/list");
		
	}
	
	@RequestMapping("/save")
	public ResponseData<Boolean> save(NewsType type, String parentId){
		if (!StringUtils.isEmpty(parentId)) {
			NewsType parentType = new NewsType();
			parentType.setId(parentId);
			type.setParent(parentType);
		}
		return service.save(type);
	}
	
	@RequestMapping("/update")
	public ResponseData<Boolean> update(NewsType type, String parentId){
		if (!StringUtils.isEmpty(parentId)) {
			NewsType parentType = new NewsType();
			parentType.setId(parentId);
			type.setParent(parentType);
		}
		return service.update(type);
	}
	
	@RequestMapping("/delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]")String[] ids){
		return service.delete(ids);
	}
	
	@RequestMapping("/findById")
	public NewsType findById(String id) {
		return service.findById(id);
	}
	
	@RequestMapping("/getTypeTree")
	public List<TreeVO> getTypeTree(String id){
		List<TreeVO> treeNodes = new LinkedList<>();
		List<NewsType> types = service.findByParent(id);
		if (!ListUtils.isListEmpty(types)) {
			for (NewsType type : types) {
				TreeVO node = new TreeVO(type.getId(), type.getName(), TreeVO.CLOSED, null);
				treeNodes.add(node);
			}
		}
		
		if (StringUtils.isEmpty(id)) {
			List<TreeVO> rootNodes = new ArrayList<>(1);
			TreeVO root = new TreeVO("", "所有栏目", TreeVO.OPEN, null);
			root.setChildren(treeNodes);
			rootNodes.add(root);
			return rootNodes;
		} else {
			return treeNodes;
		}
	}
	
	@RequestMapping("/findNewsTypes")
	public PageVO<NewsType> findNewsTypes(String name, String parentId, int page, int rows){
		Page<NewsType> pageData = service.findNewsTypes(name, parentId, page, rows);
		PageVO<NewsType> pageVo = new PageVO<>(pageData);
		return pageVo;
	}
}
