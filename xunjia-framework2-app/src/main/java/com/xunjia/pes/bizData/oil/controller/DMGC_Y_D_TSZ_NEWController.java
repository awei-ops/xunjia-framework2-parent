package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_TSZ_NEW;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_TSZ_NEWService;
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
import java.util.List;

@RestController
@RequestMapping("/dmgc_y_d_tsz_new")
public class DMGC_Y_D_TSZ_NEWController {

    @Autowired
    private DMGC_Y_D_TSZ_NEWService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_d_tsz_new_list");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessList")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public ModelAndView toAssessList() {
        return new ModelAndView("bizData/oil/y_d_tsz_new_assessment");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/chart");
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public Boolean saveData(String id, Double zhdh, Double dyohd, Double dyohq, Double dyehd, Double dyehq) {
        return service.saveData(id, zhdh, dyohd, dyohq, dyehd, dyehq);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public PageVO<DMGC_Y_D_TSZ_NEW> getPageData(DMGC_Y_D_TSZ_NEW example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_y_d_tsz_new:export")
    public void exportData(DMGC_Y_D_TSZ_NEW example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @RequestMapping("/getDataOfDay")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public List<DMGC_Y_D_TSZ_NEW> getDataOfDay(String startDate, String endDate) {
        return service.getDataOfDay(startDate, endDate);
    }

    @RequestMapping("/getSumDataOfMonth")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public List<DMGC_Y_D_TSZ_NEW> getSumDataOfMonth(String startDate, String endDate) {
        return service.getSumDataOfMonth(startDate, endDate);
    }

    @RequestMapping("/getSumDataOfYear")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public List<DMGC_Y_D_TSZ_NEW> getSumDataOfYear(String startDate, String endDate) {
        return service.getSumDataOfYear(startDate, endDate);
    }

    @RequestMapping("/getStatistics")
    @RequiresPermissions("dmgc_y_d_tsz_new:list")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        return service.getStatistics(startDate, endDate, cycle, type);
    }

    @RequestMapping("/update_DMGC_Y_D_TSZ_NEW")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
