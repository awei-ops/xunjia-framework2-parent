package com.xunjia.pes.bizData.waterInjection.controller;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_JB;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_JBService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/dmgc_s_jb")
public class DMGC_S_JBController {

    @Autowired
    private DMGC_S_JBService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("dmgc_s_jb:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/waterInjection/s_jb_list");
    }

    @RequestMapping("/getPageData")
    @RequiresPermissions("dmgc_s_jb:list")
    public PageVO<DMGC_S_JB> getPageData(DMGC_S_JB example, int page, int rows){
        return service.getPageData(example, page, rows);
    }

    @ApiOperation(value = "根据注水站Id获取泵", httpMethod = "GET")
    @RequestMapping("/getByZszId")
    public List<DMGC_S_JB> getByZszId(String zszId){
        return service.getByZszId(zszId);
    }
}
