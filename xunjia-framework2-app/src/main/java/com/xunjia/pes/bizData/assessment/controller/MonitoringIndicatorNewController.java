package com.xunjia.pes.bizData.assessment.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
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
@RequestMapping("/monitoringIndicatorNew")
public class MonitoringIndicatorNewController {
    @Autowired
    private MonitoringIndicatorNewService service;

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("monitoringIndicatorNew:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/monitoringIndicatorNew/list");
    }

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("monitoringIndicatorNew:save")
    public ModelAndView toAdd() {
        return new ModelAndView("bizData/monitoringIndicatorNew/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("monitoringIndicatorNew:update")
    public ModelAndView toEdit() {
        return new ModelAndView("bizData/monitoringIndicatorNew/edit");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("monitoringIndicatorNew:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "监测项目与指标要求") MonitoringIndicatorNew param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("monitoringIndicatorNew:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "监测项目与指标要求") MonitoringIndicatorNew param) {
        return service.update(param);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("monitoringIndicatorNew:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public MonitoringIndicatorNew findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "根据参数查询", httpMethod = "GET")
    @RequestMapping("/findByParams")
    public List<MonitoringIndicatorNew> findByParams(String type, String monitoringItem) {
        return service.findByParams(type, monitoringItem);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/getPageData")
    @RequiresPermissions("monitoringIndicatorNew:list")
    public PageVO<MonitoringIndicatorNew> getPageData(String type, int page, int rows){
        return service.getPageData(type,page,rows);
    }
}
