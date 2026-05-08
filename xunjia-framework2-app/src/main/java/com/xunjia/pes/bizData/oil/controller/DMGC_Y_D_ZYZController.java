package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZYZ;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_ZYZService;
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

@RestController
@RequestMapping("/dmgc_y_d_zyz")
public class DMGC_Y_D_ZYZController {

    @Autowired
    private DMGC_Y_D_ZYZService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_d_zyz:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_d_zyz_list");
    }

    @RequestMapping("/saveData")
    @RequiresPermissions("dmgc_y_d_zyz:list")
    public Boolean saveData(String id, Double zhdh, Double dyhd, Double dyhq) {
        return service.saveData(id, zhdh, dyhd, dyhq);
    }

    @RequestMapping("/create_DMGC_Y_D_ZYZ")
    public Boolean create(@RequestParam(required = false)String rq){
        return service.createData(rq);
    }

    @RequestMapping("/auditData")
    @RequiresPermissions("dmgc_y_d_zyz:list")
    public Boolean auditData(String rq) {
        return service.auditData(rq);
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_d_zyz:list")
    public PageVO<DMGC_Y_D_ZYZ> getPageData(DMGC_Y_D_ZYZ example, String startDate, String endDate, int page, int rows) {
        return service.getPageData(example, startDate, endDate, page, rows);
    }

    @GetMapping("/exportData")
//    @RequiresPermissions("dmgc_y_d_zyz:export")
    public void exportData(DMGC_Y_D_ZYZ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        service.exportData(example, startDate, endDate, request, response);
    }

    @RequestMapping("/update_DMGC_Y_D_ZYZ")
    public Boolean updateData(@RequestParam(required = false) String rq) {
        return service.updateData(rq);
    }
}
