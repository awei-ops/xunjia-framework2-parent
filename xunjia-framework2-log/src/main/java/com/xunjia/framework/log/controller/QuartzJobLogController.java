package com.xunjia.framework.log.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.log.entity.QuartzJobLog;
import com.xunjia.framework.log.service.QuartzJobLogService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 任务调度日志控制器
 * 2023年4月14日
 * @author 姜浩
 */
@Api(value = "操作日志控制器")
@RestController
@RequestMapping("/quartzJobLogs")
public class QuartzJobLogController {

    @Autowired
    private QuartzJobLogService quartzJobLogService;

    @GetMapping("/toList")
    public ModelAndView toList(){
        return new ModelAndView("framework/log/quartzJobLog/list");
    }

    @GetMapping("/toView")
    public ModelAndView toView(){
        return new ModelAndView("framework/log/quartzJobLog/view");
    }

    @GetMapping("/findQuartzJobLogs")
    public PageVO<QuartzJobLog> findOperateLogs(String startDate, String endDate, String jobName, Boolean executeResult, int page, int rows){
        return new PageVO<>(quartzJobLogService.findQuartzJobLogs(startDate, endDate, jobName, executeResult, page, rows));
    }

    @GetMapping("/findById")
    public QuartzJobLog findById(String id){
        return quartzJobLogService.findById(id);
    }
}
