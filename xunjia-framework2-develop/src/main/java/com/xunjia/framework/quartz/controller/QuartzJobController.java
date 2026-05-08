package com.xunjia.framework.quartz.controller;

import com.xunjia.framework.common.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.quartz.entity.QuartzJob;
import com.xunjia.framework.quartz.service.QuartzJobService;

@RestController
@RequestMapping("/quartzJob")
public class QuartzJobController {

	@Autowired
	private QuartzJobService service;

	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/quartz/add");
	}

	@RequestMapping("/toEdit")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/quartz/edit");
	}

	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/quartz/list");
	}

	@RequestMapping("/save")
	public ResponseData<Boolean> save(QuartzJob job){
		return service.save(job);
	}

	@RequestMapping("/update")
	public ResponseData<Boolean> update(QuartzJob job){
		return service.update(job);
	}
	
	@RequestMapping("/delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]")String[] ids){
		return service.delete(ids);
	}

	@RequestMapping("/start")
	public ResponseData<Boolean> start(String id){
		return service.start(id);
	}
	
	@RequestMapping("/pause")
	public ResponseData<Boolean> pause(String id){
		return service.pause(id);
	}
	
	@RequestMapping("/findById")
	public QuartzJob findById(String id) {
		return service.findById(id);
	}
	
	@RequestMapping("/findQuartzJobs")
	public PageVO<QuartzJob> findQuartzJobs(String name, int page, int rows){
		Page<QuartzJob> pageData = service.findQuartzJobs(name, page, rows);
		PageVO<QuartzJob> pageVo = new PageVO<QuartzJob>(pageData);
		return pageVo;
	}
}
