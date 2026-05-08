package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_LHZ;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_LHZService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/dmgc_y_lhz")
public class DMGC_Y_LHZController {

    @Autowired
    private DMGC_Y_LHZService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_lhz:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_lhz_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_lhz:list")
    public PageVO<DMGC_Y_LHZ> getPageData(DMGC_Y_LHZ example, int page, int rows){
        return service.getPageData(example, page, rows);
    }
}
