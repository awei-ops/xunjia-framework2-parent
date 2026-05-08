package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_ZSZService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/dmgc_s_zsz")
public class DMGC_S_ZSZController {

    @Autowired
    private DMGC_S_ZSZService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_zsz:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/s_zsz_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_zsz:list")
    public PageVO<DMGC_S_ZSZ> getPageData(DMGC_S_ZSZ example, int page, int rows){
        return service.getPageData(example, page, rows);
    }
    @ApiOperation(value = "获取所有站", httpMethod = "GET")
    @RequestMapping("/getAll")
    public List<DMGC_S_ZSZ> getAll(){
        return service.getAll();
    }
}
