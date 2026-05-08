package com.xunjia.pes.bizData.waterTreatment.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SBYX;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SBYXService;
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
@RequestMapping("/dmgc_s_d_sbyx")
public class DMGC_S_D_SBYXController {

    @Autowired
    private DMGC_S_D_SBYXService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_d_sbyx:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterTreatment/s_d_sbyx_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_d_sbyx:list")
    public PageVO<DMGC_S_D_SBYX> getPageData(DMGC_S_D_SBYX example, String startDate, String endDate, int page, int rows){
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_s_d_sbyx:export")
    public void exportData(DMGC_S_D_SBYX example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response){
        service.exportData(example, startDate, endDate, request, response);
    }
}
