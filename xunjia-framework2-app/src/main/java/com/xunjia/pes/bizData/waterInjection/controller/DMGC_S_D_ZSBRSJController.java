package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSBRSJService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/dmgc_s_d_zsbrsj")
public class DMGC_S_D_ZSBRSJController {

    @Autowired
    private DMGC_S_D_ZSBRSJService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/s_d_zsbrsj_list");
    }

    @ApiOperation(value = "跳转至日绩效列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("dmgc_s_d_zsbrsj:assessment")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/waterInjection/s_d_zsbrsj_assessment");
    }

    @ApiOperation(value = "跳转至月数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_month")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public ModelAndView toAssessment_month() {
        return new ModelAndView("bizData/waterInjection/s_d_zsbrsj_assessment_month");
    }

    @ApiOperation(value = "跳转至年数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_year")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public ModelAndView toAssessment_year() {
        return new ModelAndView("bizData/waterInjection/s_d_zsbrsj_assessment_year");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toLineChart")
    public ModelAndView toLineChart() {
        return new ModelAndView("bizData/waterInjection/lineChart");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/waterInjection/radarChart");
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_s_d_zsbrsj:assessment")
    public PageVO<DMGC_S_D_ZSBRSJ> getAssessment(String cycle, String assessmentDate, int page, int rows, DMGC_S_D_ZSBRSJ example) {
        return service.getAssessment(cycle, assessmentDate, page, rows, example);
    }

    @RequestMapping("/saveHll")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public Boolean saveHll(String id, String yxzt, Double bx, Double bckyl, Double bsshgyl, Double ll, Double hll) {
        return service.saveHll(id, yxzt, bx, bckyl, bsshgyl, ll, hll);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getIfSomeDataNotInput")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public Boolean getIfSomeDataNotInput(String rq) {
        return service.getIfSomeDataNotInput(rq);
    }

    @RequestMapping("/getAssessmentNoPage")
    @RequiresPermissions("dmgc_y_d_jrl:list")
    public List<DMGC_S_D_ZSBRSJ> getAssessmentNoPage(String cycle, String assessmentDate) {
        return service.getAssessmentNoPage(cycle, assessmentDate);
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public PageVO<DMGC_S_D_ZSBRSJ> getPageData(DMGC_S_D_ZSBRSJ example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_s_d_zsbrsj:export")
    public void exportData(DMGC_S_D_ZSBRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @GetMapping("/exportExamineData")
    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response){
        service.exportExamineData(queryDate,request,response);
    }

    @RequestMapping("/getAllStaticsOfPipe")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public List<PieOption> getAllStaticsOfPipe(String rq) {
        return service.getAllStaticsOfPipe(rq);
    }

    @RequestMapping("/getStatistics")
    @RequiresPermissions("dmgc_s_d_zsbrsj:list")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jbId) {
        return service.getStatistics(startDate, endDate, cycle, type, jbId);
    }

    @RequestMapping("/update_DMGC_S_D_ZSBRSJ")
    public Boolean updateData() {
        return service.updateData();
    }
}
