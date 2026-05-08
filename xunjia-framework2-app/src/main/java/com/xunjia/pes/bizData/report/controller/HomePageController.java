package com.xunjia.pes.bizData.report.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.report.entity.HomePage;
import com.xunjia.pes.bizData.report.service.HomePageService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/homePage")
public class HomePageController {
    @Autowired
    private HomePageService service;

    @ApiOperation(value = "跳转至报表表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("homePage:report")
    public ModelAndView toList() {
        return new ModelAndView("bizData/report/home_page");
    }

    @RequestMapping("/add")
    public ResponseData<Boolean> add(HomePage homePage) {
        return service.add(homePage);
    }

    @RequestMapping("/update")
    public ResponseData<Boolean> update(HomePage homePage) {
        return service.update(homePage);
    }

    @RequestMapping("/queryReport")
    public List<HomePage> buildReportSchema(@RequestParam Integer year) {
        List<HomePage> result = new ArrayList<>();
        result.add(service.buildReportSchema(year));
        return result;
    }
    @RequestMapping("/getStatistics")
    public PieOption getStatistics(HomePage homePage){
        return service.getStatistics(homePage);
    }
}
