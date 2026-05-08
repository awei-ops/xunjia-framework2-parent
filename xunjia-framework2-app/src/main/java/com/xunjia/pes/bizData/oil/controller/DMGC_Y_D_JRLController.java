package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_JRLService;
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

@RequestMapping("/dmgc_y_d_jrl")
public class DMGC_Y_D_JRLController {

    @Autowired
    private DMGC_Y_D_JRLService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/jiaReLu/y_d_jrl_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public PageVO<DMGC_Y_D_JRL> getPageData(DMGC_Y_D_JRL example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_y_d_jrl:export")
    public void exportData(DMGC_Y_D_JRL example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @GetMapping("/exportExamineData")
    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response){
        service.exportExamineData(queryDate,request,response);
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toJrlAssessmentList")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public ModelAndView toJrlAssessmentList() {
        return new ModelAndView("bizData/oil/jiaReLu/jrlAssessmentList");
    }

    @ApiOperation(value = "跳转至月数据列表页", httpMethod = "GET")
    @RequestMapping("/toJrlAssessmentList_month")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public ModelAndView toJrlAssessmentList_month() {
        return new ModelAndView("bizData/oil/jiaReLu/jrlAssessmentList_month");
    }

    @ApiOperation(value = "跳转至年数据列表页", httpMethod = "GET")
    @RequestMapping("/toJrlAssessmentList_year")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public ModelAndView toJrlAssessmentList_year() {
        return new ModelAndView("bizData/oil/jiaReLu/jrlAssessmentList_year");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/oil/jiaReLu/radarChart");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toLineChart")
    public ModelAndView toLineChart() {
        return new ModelAndView("bizData/oil/jiaReLu/lineChart");
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public PageVO<DMGC_Y_D_JRL> getAssessment(String cycle, String assessmentDate, int page, int rows,DMGC_Y_D_JRL example) {
        return service.getAssessment(cycle, assessmentDate, page, rows,example);
    }

    @RequestMapping("/getAssessmentNoPage")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public List<DMGC_Y_D_JRL> getAssessmentNoPage(String cycle, String assessmentDate) {
        return service.getAssessmentNoPage(cycle, assessmentDate);
    }

    @RequestMapping("/saveLtbmwd")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public Boolean saveLtbmwd(String id, String jrlyxzk, Double lx, Integer yqwd, Double yqhyl, Integer jrl, Double ltwbmwd) {
        return service.saveLtbmwd(id, jrlyxzk, lx, yqwd, yqhyl, jrl, ltwbmwd);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getIfSomeDataNotInput")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public Boolean getIfSomeDataNotInput(String rq){return service.getIfSomeDataNotInput(rq);}


    @RequestMapping("/getAllStaticsOfPipe")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public List<PieOption> getAllStaticsOfPipe(String rq) {
        return service.getAllStaticsOfPipe(rq);
    }

    @RequestMapping("/getStatistics")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jrlId) {
        return service.getStatistics(startDate, endDate, cycle, type, jrlId);
    }

    @RequestMapping("/update_DMGC_Y_D_JRL")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
