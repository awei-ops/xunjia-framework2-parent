package com.xunjia.framework.logback.controller;

import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.LoggingEvent;
import com.xunjia.framework.usermanage.entity.LoggingEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.logback.service.LoggingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="系统日志控制器")
@RestController
@RequestMapping("/log")
public class LogController {

	@Autowired
	private LoggingService service;

	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/log/list");
	}

	@ApiOperation(value="根据事件id查询异常信息分页数据", httpMethod="GET")
	@RequestMapping("/findExceptionByEventId")
	public PageVO<LoggingEventException> findExceptionByEventId(@ApiParam(value="事件id")long eventId){
		List<LoggingEventException> list = service.findExceptionByEventId(eventId);
		return new PageVO<LoggingEventException>(list.size(), list);
	}

	@ApiOperation(value="查询系统事件分页数据", httpMethod="GET")
	@RequestMapping("/findLoggingEvent")
	public PageVO<LoggingEvent> findLoggingEvent(
			@ApiParam(value="起始时间")String startDate, 
			@ApiParam(value="截止时间")String endDate, 
			@ApiParam(value="事件级别")String level, 
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		PageVO<LoggingEvent> pageVo = null;
		Page<LoggingEvent> pageData = service.findLoggingEvent(startDate, endDate, level, page, rows);
		if (pageData != null) {
			pageVo = new PageVO<LoggingEvent>(pageData.getTotalElements(), pageData.getContent());
		} else {
			pageVo = new PageVO<LoggingEvent>(0, new ArrayList<LoggingEvent>(0));
		}
		return pageVo;
	}
}
