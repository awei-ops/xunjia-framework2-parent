package com.xunjia.pes.bizData.waterTreatment.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_SCLZService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/dmgc_s_sclz")
public class DMGC_S_SCLZController {

    @Autowired
    private DMGC_S_SCLZService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_sclz:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterTreatment/s_sclz_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_sclz:list")
    public PageVO<DMGC_S_SCLZ> getPageData(DMGC_S_SCLZ example, int page, int rows){
        return service.getPageData(example, page, rows);
    }

    @ApiOperation(value = "获取所有站", httpMethod = "GET")
    @RequestMapping("/getAll")
    public List<DMGC_S_SCLZ> getAll(){
        return service.getAll();
    }
}
