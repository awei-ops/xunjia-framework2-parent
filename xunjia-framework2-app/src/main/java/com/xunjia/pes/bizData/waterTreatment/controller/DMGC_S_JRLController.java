package com.xunjia.pes.bizData.waterTreatment.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_JRL;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_JRLService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/dmgc_s_jrl")
public class DMGC_S_JRLController {

    @Autowired
    private DMGC_S_JRLService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_jrl:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterTreatment/s_jrl_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_jrl:list")
    public PageVO<DMGC_S_JRL> getPageData(DMGC_S_JRL example, int page, int rows){
        return service.getPageData(example, page, rows);
    }
}
