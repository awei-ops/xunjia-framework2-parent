package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSZRSJService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/dmgc_s_d_zszrsj")
public class DMGC_S_D_ZSZRSJController {

    @Autowired
    private DMGC_S_D_ZSZRSJService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_d_zszrsj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/s_d_zszrsj_list");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/chart");
    }

    @ApiOperation(value = "跳转至绩效列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("dmgc_s_d_zszrsj:assessment")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/waterInjection/s_d_zszrsj_assessment");
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_s_d_zszrsj:list")
    public Boolean saveData(String id, Double zhdh) {
        return service.saveData(id, zhdh);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_s_d_zszrsj:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_d_zszrsj:list")
    public PageVO<DMGC_S_D_ZSZRSJ> getPageData(DMGC_S_D_ZSZRSJ example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_s_d_zszrsj:export")
    public void exportData(DMGC_S_D_ZSZRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @GetMapping("/getStatistics")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        return service.getStatistics(startDate, endDate, cycle, type);
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_s_d_zszrsj:assessment")
    public PageVO<DMGC_S_D_ZSZRSJ> getAssessment(String cycle, String assessmentDate, int page, int rows) {
        return service.getAssessment(cycle, assessmentDate, page, rows);
    }

    @RequestMapping("/update_DMGC_S_D_ZSZRSJ")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
