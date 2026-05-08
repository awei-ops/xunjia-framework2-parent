package com.xunjia.framework.news.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.news.entity.News;
import com.xunjia.framework.news.entity.NewsContent;
import com.xunjia.framework.news.entity.NewsType;
import com.xunjia.framework.news.service.NewsService;
import com.xunjia.framework.news.service.NewsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.Context;

@RestController
@RequestMapping("/news")
public class NewsController {

	@Autowired
	private NewsService service;

	@Autowired
	private NewsTypeService typeService;

	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("news/news/add");
	}

	@RequestMapping("/toEdit")
	public ModelAndView toEdit(String id) {
		ModelAndView mav = new ModelAndView("news/news/edit");
		NewsContent newsContent = service.findContentById(id);
		mav.addObject("data", newsContent);
		return mav;
	}

	@RequestMapping("/toList")
	public ModelAndView toList() {
		ModelAndView mav = new ModelAndView("news/news/list");
		return mav;
	}

	@RequestMapping("/toReadList")
	public ModelAndView toReadList(String typeName){
		ModelAndView mav = new ModelAndView("news/news/readList");
		NewsType newsType = typeService.findByName(typeName);
		mav.addObject("typeId", newsType.getId());
		return mav;
	}

	@RequestMapping("/read")
	public ModelAndView read(String id){
		ModelAndView mav = new ModelAndView("news/news/read");
		NewsContent newsContent = service.findContentByIdForRead(id);
		mav.addObject("news", newsContent);
		return mav;
	}

	@RequestMapping("/save")
	public ResponseData<Boolean> save(News news, String content){
		return service.save(news, content);
	}
	
	@RequestMapping("/update")
	public ResponseData<Boolean> update(News news, String content){
		return service.update(news, content);
	}
	
	@RequestMapping("/delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]")String[] ids){
		return service.delete(ids);
	}
	
	@RequestMapping("/audit")
	public ResponseData<Boolean> audit(@RequestParam(name="ids[]")String[] ids){
		return service.audit(1, ids);
	}
	
	@RequestMapping("/findContentById")
	public NewsContent findContentById(String id) {
		return service.findContentById(id);
	}
	
	@RequestMapping("/findNews")
	public PageVO<News> findNews(String title, String startDate, String endDate, @RequestParam(defaultValue = "-1")int auditState, String typeId, int page, int rows){
		String myName = Context.getCurrentUser().getRealName();
		Page<News> pageData = service.findNews(title, startDate, endDate, auditState, typeId, myName, page, rows);
		PageVO<News> pageVo = new PageVO<>(pageData);
		return pageVo;
	}
}
