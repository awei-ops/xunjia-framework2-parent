package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZSBYXSS;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_ZSBYXSSService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/dmgc_y_d_zsbyxss")
public class DMGC_Y_D_ZSBYXSSController {

    @Autowired
    private DMGC_Y_D_ZSBYXSSService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_d_zsbyxss:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_d_zsbyxss_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_d_zsbyxss:list")
    public PageVO<DMGC_Y_D_ZSBYXSS> getPageData(DMGC_Y_D_ZSBYXSS example, String startDate, String endDate, int page, int rows){
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_y_d_zsbyxss:export")
    public void exportData(DMGC_Y_D_ZSBYXSS example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response){
        service.exportData(example, startDate, endDate, request, response);
    }
}
