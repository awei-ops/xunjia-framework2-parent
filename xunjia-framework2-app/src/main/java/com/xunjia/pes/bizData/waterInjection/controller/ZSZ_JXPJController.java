package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
import com.xunjia.pes.bizData.waterInjection.service.ZSZ_JXPJService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@RestController
@RequestMapping("/zsz_jxpj")
public class ZSZ_JXPJController {
    @Autowired
    private ZSZ_JXPJService service;
    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("zsz_jxpj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/zsz_jxpj_list");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toRelativeJXPJ")
    @RequiresPermissions("zsz_jxpj:list")
    public ModelAndView toRelativeJXPJ() {
        return new ModelAndView("bizData/waterInjection/zsz_relative_jxpj");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toPortraitJXPJ")
    @RequiresPermissions("zsz_jxpj:list")
    public ModelAndView toPortraitJXPJ() {
        return new ModelAndView("bizData/waterInjection/zsz_bar_chart");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toComprehensiveJXPJ")
    @RequiresPermissions("zsz_jxpj:list")
    public ModelAndView toComprehensiveJXPJ() {
        return new ModelAndView("bizData/waterInjection/zsz_comprehensive_jxpj");
    }

    @RequestMapping("/getZSZJXPJ")
    public List<Station_pj> getZSZJXPJ(String cycle, String assessmentDate){
        List<Station_pj> result =service.getZSZJXPJ(cycle,assessmentDate);
        return result;
    }

    @RequestMapping("/getRelativeJXPJ")
    public List<Station_pj> getRelativeJXPJ(String cycle, String zid, String queryDateString){
        return service.getRelativeJXPJ(cycle,zid,queryDateString);
    }

    @RequestMapping("/getSinglePortraitJXPJ")
    public List<Station_pj> getSinglePortraitJXPJ(String cycle, String zid, String queryDateString){
        return service.getSinglePortraitJXPJ(cycle,zid,queryDateString);
    }

    @RequestMapping("/getSingleComprehensiveJXPJ")
    public List<Station_pj> getSingleComprehensiveJXPJ(String cycle, String zid, String queryDateString){
        return service.getSingleComprehensiveJXPJ(cycle,zid,queryDateString);
    }

    @RequestMapping("/getPortraitJXPJ")
    public List<Station_pj> getPortraitJXPJ(String zid, String queryStart, String queryEnd){
        List<Station_pj> result = service.getPortraitJXPJ(zid,queryStart,queryEnd);
        return result;
    }

    @RequestMapping("/getComprehensiveJXPJ")
    public List<Station_pj> getComprehensiveJXPJ(String zid, String queryStart, String queryEnd){
        List<Station_pj> result = service.getComprehensiveJXPJ(zid,queryStart,queryEnd);
        return result;
    }

    @RequestMapping("/update_Station_pj")
    public Boolean updateData(@RequestParam(required = false)  String rq){
        return service.updateData(rq);
    }
}
