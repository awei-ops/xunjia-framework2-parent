package com.xunjia.pes.bizData.assessment.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicator;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorService;
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
@RequestMapping("/monitoringIndicator")
public class MonitoringIndicatorController {
    @Autowired
    private MonitoringIndicatorService service;

    @ApiOperation(value = "跳转至数据列表页（注水泵）", httpMethod = "GET")
    @RequestMapping("/toCentrifugalPumpList")
    @RequiresPermissions("monitoringIndicator:list")
    public ModelAndView toCentrifugalPumpList() {
        return new ModelAndView("bizData/monitoringIndicator/centrifugalPumpList");
    }

    @ApiOperation(value = "跳转至添加页面（注水泵）", httpMethod = "GET")
    @RequestMapping("/toAddCentrifugalPump")
    @RequiresPermissions("monitoringIndicator:save")
    public ModelAndView toAddCentrifugalPump() {
        return new ModelAndView("bizData/monitoringIndicator/addCentrifugalPump");
    }

    @ApiOperation(value = "跳转至编辑页面（注水泵）", httpMethod = "GET")
    @RequestMapping("/toEditCentrifugalPump")
    @RequiresPermissions("monitoringIndicator:update")
    public ModelAndView toEditCentrifugalPump() {
        return new ModelAndView("bizData/monitoringIndicator/editCentrifugalPump");
    }

    @ApiOperation(value = "跳转至数据列表页（往复泵）", httpMethod = "GET")
    @RequestMapping("/toReciprocatingPumpList")
    @RequiresPermissions("monitoringIndicator:list")
    public ModelAndView toReciprocatingPumpList() {
        return new ModelAndView("bizData/monitoringIndicator/reciprocatingPumpList");
    }

    @ApiOperation(value = "跳转至添加页面（往复泵）", httpMethod = "GET")
    @RequestMapping("/toAddReciprocatingPump")
    @RequiresPermissions("monitoringIndicator:save")
    public ModelAndView toAddReciprocatingPump() {
        return new ModelAndView("bizData/monitoringIndicator/addReciprocatingPump");
    }

    @ApiOperation(value = "跳转至编辑页面（往复泵）", httpMethod = "GET")
    @RequestMapping("/toEditReciprocatingPump")
    @RequiresPermissions("monitoringIndicator:update")
    public ModelAndView toEditReciprocatingPump() {
        return new ModelAndView("bizData/monitoringIndicator/editReciprocatingPump");
    }

    @ApiOperation(value = "跳转至数据列表页（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toFiredHeaterList")
    @RequiresPermissions("monitoringIndicator:list")
    public ModelAndView toOtherList() {
        return new ModelAndView("bizData/monitoringIndicator/gasFiredHeaterList");
    }

    @ApiOperation(value = "跳转至添加页面（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toAddGasFiredHeater")
    @RequiresPermissions("monitoringIndicator:save")
    public ModelAndView toAddOtherPump() {
        return new ModelAndView("bizData/monitoringIndicator/addGasFiredHeater");
    }

    @ApiOperation(value = "跳转至编辑页面（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toEditGasFiredHeater")
    @RequiresPermissions("monitoringIndicator:update")
    public ModelAndView toEditOtherPump() {
        return new ModelAndView("bizData/monitoringIndicator/editGasFiredHeater");
    }

    @ApiOperation(value = "跳转至数据列表页（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toSybList")
    @RequiresPermissions("monitoringIndicator:list")
    public ModelAndView toSybList() {
        return new ModelAndView("bizData/monitoringIndicator/sybPumpList");
    }

    @ApiOperation(value = "跳转至添加页面（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toAddSyb")
    @RequiresPermissions("monitoringIndicator:save")
    public ModelAndView toAddSyb() {
        return new ModelAndView("bizData/monitoringIndicator/addSyblPump");
    }

    @ApiOperation(value = "跳转至编辑页面（燃气加热炉）", httpMethod = "GET")
    @RequestMapping("/toEditSyb")
    @RequiresPermissions("monitoringIndicator:update")
    public ModelAndView toEditSyb() {
        return new ModelAndView("bizData/monitoringIndicator/editSybPump");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("monitoringIndicator:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "监测项目与指标要求") MonitoringIndicator param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("monitoringIndicator:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "监测项目与指标要求") MonitoringIndicator param) {
        return service.update(param);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("monitoringIndicator:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public MonitoringIndicator findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "根据类型及监测项目1关键字查询", httpMethod = "GET")
    @RequestMapping("/finByTypeAndItemOneKey")
    public List<MonitoringIndicator> finByTypeAndItemOneKey(String type, String itemOneKey) {
        return service.finByTypeAndItemOneKey(type, itemOneKey);
    }
}
