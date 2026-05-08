package com.xunjia.pes.bizData.waterTreatment.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SCLZRSJService;
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
@RequestMapping("/dmgc_s_d_sclzrsj")
public class DMGC_S_D_SCLZRSJController {

    @Autowired
    private DMGC_S_D_SCLZRSJService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_d_sclzrsj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterTreatment/s_d_sclzrsj_list");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/chart");
    }

    @ApiOperation(value = "跳转至单耗绩效列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("dmgc_s_d_sclzrsj:assessment")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/waterTreatment/s_d_sclz_assessment");
    }

    @ApiOperation(value = "跳转至污水单耗绩效列表页", httpMethod = "GET")
    @RequestMapping("/toWsAssessment")
    @RequiresPermissions("dmgc_s_d_sclzrsj:assessment")
    public ModelAndView toWsAssessment() {
        return new ModelAndView("bizData/waterTreatment/s_d_sclz_ws_assessment");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_d_sclzrsj:list")
    public PageVO<DMGC_S_D_SCLZRSJ> getPageData(DMGC_S_D_SCLZRSJ example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_s_d_sclzrsj:list")
    public Boolean saveData(String id, Double dh, Double rhdl, Double rwssl) {
        return service.saveData(id, dh, rhdl, rwssl);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_s_d_sclzrsj:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_s_d_sclzrsj:export")
    public void exportData(DMGC_S_D_SCLZRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @GetMapping("/getStatistics")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        return service.getStatistics(startDate, endDate, cycle, type);
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_s_d_sclzrsj:assessment")
    public PageVO<DMGC_S_D_SCLZRSJ> getAssessment(String cycle, String assessmentDate, int page, int rows) {
        return service.getAssessment(cycle, assessmentDate, page, rows);
    }

    @RequestMapping("/update_DMGC_S_D_SCLZRSJ")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
