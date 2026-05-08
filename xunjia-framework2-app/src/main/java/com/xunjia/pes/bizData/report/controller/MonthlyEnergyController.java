package com.xunjia.pes.bizData.report.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.report.entity.MonthlyEnergy;
import com.xunjia.pes.bizData.report.service.MonthlyEnergyService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/monthlyEnergy")
public class MonthlyEnergyController {
    @Autowired
    private MonthlyEnergyService service;

    @ApiOperation(value = "跳转至报表表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("monthlyEnergy:report")
    public ModelAndView toList() {
        return new ModelAndView("bizData/report/monthly_energy");
    }

    @ApiOperation(value = "跳转至图表页", httpMethod = "GET")
    @RequestMapping("/toChart")
    public ModelAndView toChart() {
        return new ModelAndView("bizData/chart");
    }

    @RequestMapping("/add")
    public ResponseData<Boolean> add(MonthlyEnergy monthlyEnergy) {
        return service.add(monthlyEnergy);
    }

    @RequestMapping("/update")
    public ResponseData<Boolean> update(MonthlyEnergy monthlyEnergy) {
        return service.update(monthlyEnergy);
    }

    @RequestMapping("/queryReport")
    public List<MonthlyEnergy> buildReportSchema(@RequestParam Integer year, @RequestParam Integer month, @RequestParam Boolean compute) {
        return service.buildReportSchema(year, month, compute);
    }
    @RequestMapping("/getStatistics")
    public ChartOption getStatistics(@RequestParam Integer year, @RequestParam Integer month){
        return service.getStatistics(year,month);
    }

    @RequestMapping("/getStatisticsUnit")
    public ChartOption getStatisticsUnit(@RequestParam Integer year, @RequestParam Integer month){
        return service.getStatisticsUnit(year,month);
    }
}
