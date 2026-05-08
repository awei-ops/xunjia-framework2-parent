package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_CSB;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_CSBService;
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
@RequestMapping("/dmgc_y_d_csb")
public class DMGC_Y_D_CSBController {
    @Autowired
    private DMGC_Y_D_CSBService service;


    @ApiOperation(value = "跳转至日绩效列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/oil/chanShuiBeng/csbAssessmentList");
    }

    @ApiOperation(value = "跳转至月数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_month")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public ModelAndView toAssessment_month() {
        return new ModelAndView("bizData/oil/chanShuiBeng/csbAssessmentList_month");
    }

    @ApiOperation(value = "跳转至年数据列表页", httpMethod = "GET")
    @RequestMapping("/toAssessment_year")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public ModelAndView toAssessment_year() {
        return new ModelAndView("bizData/oil/chanShuiBeng/csbAssessmentList_year");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toLineChart")
    public ModelAndView toLineChart() {
        return new ModelAndView("bizData/oil/chanShuiBeng/lineChart");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/oil/chanShuiBeng/radarChart");
    }

    @RequestMapping("/getAssessment")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public PageVO<DMGC_Y_D_CSB> getAssessment(String cycle, String assessmentDate, int page, int rows,DMGC_Y_D_CSB example) {
        return service.getAssessment(cycle, assessmentDate, page, rows,example);
    }

    @RequestMapping("/getAssessmentNoPage")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public List<DMGC_Y_D_CSB> getAssessmentNoPage(String cycle, String assessmentDate) {
        return service.getAssessmentNoPage(cycle, assessmentDate);
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public Boolean saveData(String id, String yxzt, Double bxl, Double jlssl, Double hlRate, Double fhl) {
        return service.saveData(id, yxzt, bxl, jlssl, hlRate, fhl);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getIfSomeDataNotInput")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public Boolean getIfSomeDataNotInput(String rq){return service.getIfSomeDataNotInput(rq);}

    @RequestMapping("/getAllStaticsOfPipe")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public List<PieOption> getAllStaticsOfPipe(String rq) {
        return service.getAllStaticsOfPipe(rq);
    }

    @RequestMapping("/getStatistics")
    @RequiresPermissions("dmgc_y_d_csb:list")
    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jbId) {
        return service.getStatistics(startDate, endDate, cycle, type, jbId);
    }

    @RequestMapping("/create_DMGC_Y_D_CSB")
    public Boolean create(@RequestParam(required = false) String rq) {
        return service.createData(rq);
    }

    @GetMapping("/exportExamineData")
    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response){
        service.exportExamineData(queryDate,request,response);
    }
}
