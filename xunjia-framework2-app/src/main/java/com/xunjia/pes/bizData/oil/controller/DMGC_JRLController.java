package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_JRL;
import com.xunjia.pes.bizData.oil.service.DMGC_JRLService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/dmgc_jrl")
public class DMGC_JRLController {

    @Autowired
    private DMGC_JRLService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_jrl:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/jiaReLu/jrl_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_jrl:list")
    public PageVO<DMGC_JRL> getPageData(DMGC_JRL example, int page, int rows){
        return service.getPageData(example, page, rows);
    }
}
