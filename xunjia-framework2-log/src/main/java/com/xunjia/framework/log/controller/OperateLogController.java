package com.xunjia.framework.log.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.log.entity.OperateLog;
import com.xunjia.framework.log.service.OperateLogService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 操作日志控制器
 * 2023年1月5日
 * @author 姜浩
 */
@Api(value = "操作日志控制器")
@RestController
@RequestMapping("/operateLogs")
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    @GetMapping("/toList")
    public ModelAndView toList(){
        return new ModelAndView("framework/log/operateLog/list");
    }

    @GetMapping("/toView")
    public ModelAndView toView(){
        return new ModelAndView("framework/log/operateLog/view");
    }

    @GetMapping("/findOperateLogs")
    public PageVO<OperateLog> findOperateLogs(String startDate, String endDate, String module, String username, int page, int rows){
        return new PageVO<>(operateLogService.findOperateLogs(startDate, endDate, module, username, page, rows));
    }

    @GetMapping("/findById")
    public OperateLog findById(String id){
        return operateLogService.findById(id);
    }
}
