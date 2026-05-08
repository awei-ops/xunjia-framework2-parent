package com.xunjia.pes.bizData.operationArea.controller;

import com.xunjia.pes.bizData.operationArea.service.ZYQ_JXPJService;
import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/zyq_jxpj")
public class ZYQ_JXPJController {
    @Autowired
    private ZYQ_JXPJService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("zyq_jxpj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/zyq/zyq_jxpj_list");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toRelativeJXPJ")
    @RequiresPermissions("zyq_jxpj:list")
    public ModelAndView toRelativeJXPJ() {
        return new ModelAndView("bizData/zyq/zyq_relative_jxpj");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toPortraitJXPJ")
    @RequiresPermissions("zyq_jxpj:list")
    public ModelAndView toPortraitJXPJ() {
        return new ModelAndView("bizData/zyq/zyq_bar_chart");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toComprehensiveJXPJ")
    @RequiresPermissions("zyq_jxpj:list")
    public ModelAndView toComprehensiveJXPJ() {
        return new ModelAndView("bizData/zyq/zyq_comprehensive_jxpj");
    }

    @RequestMapping("/getZYQJXPJ")
    public List<Station_pj> getZYQJXPJ(String cycle, String assessmentDate) {
        List<Station_pj> test = service.getZYQJXPJ(cycle, assessmentDate);
        return test;
    }

    @RequestMapping("/getRelativeJXPJ")
    public List<Station_pj> getRelativeJXPJ(String cycle, String queryDateString) {
        return service.getRelativeJXPJ(cycle, queryDateString);
    }

    @RequestMapping("/getSinglePortraitJXPJ")
    public List<Station_pj> getSinglePortraitJXPJ(String cycle, String queryDateString) {
        return service.getSinglePortraitJXPJ(cycle, queryDateString);
    }

    @RequestMapping("/getSingleComprehensiveJXPJ")
    public List<Station_pj> getSingleComprehensiveJXPJ(String cycle, String queryDateString) {
        return service.getSingleComprehensiveJXPJ(cycle, queryDateString);
    }

    @RequestMapping("/create_ZYQ_RSJ")
    public Boolean createData(@RequestParam(required = false) String rq) {
        return service.createData(rq);
    }

    @RequestMapping("/update_ZYQ_RSJ")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
