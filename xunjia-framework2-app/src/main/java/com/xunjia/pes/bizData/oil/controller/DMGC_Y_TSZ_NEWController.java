package com.xunjia.pes.bizData.oil.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_TSZ_NEW;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_ZYZ;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_TSZ_NEWService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/dmgc_y_tsz_new")
public class DMGC_Y_TSZ_NEWController {

    @Autowired
    private DMGC_Y_TSZ_NEWService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_y_tsz_new:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/oil/y_tsz_new_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_y_tsz_new:list")
    public PageVO<DMGC_Y_TSZ_NEW> getPageData(DMGC_Y_TSZ_NEW example, int page, int rows){
        return service.getPageData(example, page, rows);
    }

    @RequestMapping("/getAll")
    public List<DMGC_Y_TSZ_NEW> getAll(){
        return service.getAll();
    }
}
