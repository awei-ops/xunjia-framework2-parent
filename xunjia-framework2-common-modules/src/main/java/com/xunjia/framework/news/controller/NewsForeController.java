package com.xunjia.framework.news.controller;

import com.xunjia.framework.news.entity.News;
import com.xunjia.framework.news.entity.NewsContent;
import com.xunjia.framework.news.entity.NewsType;
import com.xunjia.framework.news.service.NewsService;
import com.xunjia.framework.news.service.NewsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/newsFore")
public class NewsForeController {

	@Autowired
	private NewsService service;
	
	@Autowired
	private NewsTypeService typeService;
	
	@RequestMapping("/showLatestNewsTitle")
	public ModelAndView showLatestNewsTitle(String type, String width, String height) {
		ModelAndView mav = new ModelAndView("news/fore/title");
		News news = service.findLatestNews(type);
		mav.addObject("news", news);
		mav.addObject("width", width);
		mav.addObject("height", height);
		return mav;
	}
	
	@RequestMapping("/newsList")
	public ModelAndView newsList(String type, int page, int rows) {
		ModelAndView mav = new ModelAndView("news/news/readList");
		NewsType newsType = typeService.findByName(type);
		Page<News> pageData = service.findNews(null, null, null, 1, newsType.getId(), null, page, rows);
		mav.addObject("pageData", pageData);
		return mav;
	}
	
	@RequestMapping("/read")
	public ModelAndView read(String id) {
		ModelAndView mav = new ModelAndView("news/news/read");
		NewsContent content = service.findContentById(id);
		service.updateReadCount(id);
		mav.addObject("data", content);
		return mav;
	}
}
