package com.xunjia.framework.user.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.LoginAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.user.service.LoginAuditService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value="用户登录审计控制器")
@RestController
@RequestMapping("/loginAudit")
public class LoginAuditController {

	@Autowired
	private LoginAuditService service;

	@ApiOperation(value="跳转至数据列表页", httpMethod="GET")
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/loginAudit/list");
	}

	@ApiOperation(value="查询登录日志分页数据", httpMethod="GET")
	@RequestMapping("/findRecords")
	public PageVO<LoginAudit> findRecords(
			@ApiParam(value="起始日期")String startDate, 
			@ApiParam(value="截止日期")String endDate, 
			@ApiParam(value="登录是否成功")String success,
			@ApiParam(value="登录来源")String from,
			@ApiParam(value="页号")int page, 
			@ApiParam(value="每页显示条数")int rows){
		if (StringUtils.isEmpty(success)) {
			success = "-1";
		}
		Page<LoginAudit> pageData = service.findLoginAuditRecords(startDate, endDate, from,
				Integer.parseInt(success), page, rows);
		PageVO<LoginAudit> pageVo = new PageVO<LoginAudit>(pageData);
		return pageVo;
	}

	@ApiOperation(value="清除登录日志", httpMethod="GET")
	@RequestMapping("/clearRecords")
	public ResponseData<Boolean> clearRecords(
			@ApiParam(value="起始日期")String startDate, 
			@ApiParam(value="截止日期")String endDate){
		return service.clear(startDate, endDate);
	}
}
