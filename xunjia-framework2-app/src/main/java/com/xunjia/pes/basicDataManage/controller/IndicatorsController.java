package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.basicDataManage.entity.IndicatorItem;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/indicators")
public class IndicatorsController {
    @Autowired
    private IndicatorsService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("indicators:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/indicators/list");
    }

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("indicators:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/indicators/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("indicators:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/indicators/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toDisplay")
    public ModelAndView toDisplay() {
        return new ModelAndView("basicDataManage/indicators/displayList");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("indicators:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "装置类型") Indicators param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("indicators:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "装置类型") Indicators param) {
        return service.update(param);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("indicators:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public Indicators findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findIndicators")
    @RequiresPermissions("indicators:list")
    public PageVO<Indicators> findIndicators(
            String typeCode,String levelCode,String itemCode, int page, int rows) {
        PageVO<Indicators> pageVo = service.getPageData(typeCode,levelCode,itemCode, page, rows);
        return pageVo;
    }

    @ApiOperation(value = "获取指标项目", httpMethod = "GET")
    @RequestMapping("/getByTypeCodeAndLevelCode")
    public List<IndicatorItem> getByTypeCodeAndLevelCode(String typeCode, String levelCode){
        return service.getByTypeCodeAndLevelCode(typeCode,levelCode);
    }
}
