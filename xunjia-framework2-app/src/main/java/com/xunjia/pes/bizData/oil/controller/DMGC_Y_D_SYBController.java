package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_SYB;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_SYBService;
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
@RequestMapping("/dmgc_y_d_syb")
public class DMGC_Y_D_SYBController {

    @Autowired
    private DMGC_Y_D_SYBService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_d_syb_list");
    }

    @ApiOperation(value = "跳转至日绩效列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("dmgc_y_d_syb:assessment")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/oil/shuYouBeng/sybAssessmentList");
    }

    @ApiOperation(value = "跳转至月数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_month")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public ModelAndView toAssessment_month() {
        return new ModelAndView("bizData/oil/shuYouBeng/sybAssessmentList_month");
    }

    @ApiOperation(value = "跳转至年数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_year")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public ModelAndView toAssessment_year() {
        return new ModelAndView("bizData/oil/shuYouBeng/sybAssessmentList_year");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toLineChart")
    public ModelAndView toLineChart() {
        return new ModelAndView("bizData/oil/shuYouBeng/lineChart");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/oil/shuYouBeng/radarChart");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public PageVO<DMGC_Y_D_SYB> getPageData(DMGC_Y_D_SYB example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_y_d_syb:export")
    public void exportData(DMGC_Y_D_SYB example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @GetMapping("/exportExamineData")
    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response){
        service.exportExamineData(queryDate,request,response);
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public PageVO<DMGC_Y_D_SYB> getAssessment(String cycle, String assessmentDate, int page, int rows,DMGC_Y_D_SYB example) {
        return service.getAssessment(cycle, assessmentDate, page, rows,example);
    }

    @RequestMapping("/getAssessmentNoPage")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public List<DMGC_Y_D_SYB> getAssessmentNoPage(String cycle, String assessmentDate) {
        return service.getAssessmentNoPage(cycle, assessmentDate);
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public Boolean saveData(String id, String sbyxzk, Double sybxl, Double ckyl, Double bsshgyl, Integer pjll, Double hll) {
        return service.saveData(id, sbyxzk, sybxl, ckyl, bsshgyl, pjll, hll);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getIfSomeDataNotInput")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public Boolean getIfSomeDataNotInput(String rq){return service.getIfSomeDataNotInput(rq);}

    @RequestMapping("/getAllStaticsOfPipe")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public List<PieOption> getAllStaticsOfPipe(String rq) {
        return service.getAllStaticsOfPipe(rq);
    }

    @RequestMapping("/getStatistics")
    @RequiresPermissions("dmgc_y_d_syb:list")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jbId) {
        return service.getStatistics(startDate, endDate, cycle, type, jbId);
    }
}
