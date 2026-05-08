package com.xunjia.pes.bizData.waterTreatment.controller;

import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
import com.xunjia.pes.bizData.waterTreatment.service.SCLZ_JXPJService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/sclz_jxpj")
public class SCLZ_JXPJController {

    @Autowired
    private SCLZ_JXPJService service;
    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("sclz_jxpj:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterTreatment/sclz_jxpj_list");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toRelativeJXPJ")
    @RequiresPermissions("sclz_jxpj:list")
    public ModelAndView toRelativeJXPJ() {
        return new ModelAndView("bizData/waterTreatment/sclz_relative_jxpj");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toPortraitJXPJ")
    @RequiresPermissions("sclz_jxpj:list")
    public ModelAndView toPortraitJXPJ() {
        return new ModelAndView("bizData/waterTreatment/sclz_bar_chart");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toComprehensiveJXPJ")
    @RequiresPermissions("sclz_jxpj:list")
    public ModelAndView toComprehensiveJXPJ() {
        return new ModelAndView("bizData/waterTreatment/sclz_comprehensive_jxpj");
    }

    @RequestMapping("/getSCLZJXPJ")
    public List<Station_pj> getSCLZJXPJ(String cycle, String assessmentDate){
        List<Station_pj> result =service.getSCLZJXPJ(cycle,assessmentDate);
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
