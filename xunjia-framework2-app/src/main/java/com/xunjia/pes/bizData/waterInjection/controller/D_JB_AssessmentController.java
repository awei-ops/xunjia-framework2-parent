package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.waterInjection.entity.D_JB_Assessment;
import com.xunjia.pes.bizData.waterInjection.service.D_JB_AssessmentService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/d_jb_assessment")
public class D_JB_AssessmentController {

    @Autowired
    private D_JB_AssessmentService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("d_jb_assessment:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/d_jb_assessment");
    }

    @ApiOperation(value = "跳转至统计页", httpMethod = "GET")
    @RequestMapping("/toAssessment")
    @RequiresPermissions("d_jb_assessment:list")
    public ModelAndView toAssessment() {
        return new ModelAndView("bizData/doubleChart");
    }

    @RequestMapping("/getData")
    @RequiresPermissions("d_jb_assessment:list")
    public List<D_JB_Assessment> query(String rq,String zszId,String jbId){
        D_JB_Assessment example = new D_JB_Assessment();
        try {
            example.setRq(DateUtils.parse(rq, DateUtils.DATE_PATTERN));
            example.setZszId(zszId);
            example.setJbId(jbId);
        }catch (Exception ex){
            String err = ex.getMessage();
        }
        return service.query(example);
    }

    @RequestMapping("/getRunningState")
    @RequiresPermissions("d_jb_assessment:list")
    public PieOption getStatisticsOfRunningState(String rq, String zszId){
        return service.getStatisticsOfRunningState(rq,zszId);
    }

    @RequestMapping("/getThrottlingLoss")
    @RequiresPermissions("d_jb_assessment:list")
    public PieOption getStatisticsOfThrottlingLoss(String rq, String zszId){
        return service.getStatisticsOfThrottlingLoss(rq,zszId);
    }
}
