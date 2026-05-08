package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.DeviceBasicData;
import com.xunjia.pes.basicDataManage.service.DeviceBasicDataService;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Api(value = "装置基础数据指标控制器")
@RestController
@RequestMapping("/deviceBasicData")
public class DeviceBasicDataController {

    @Autowired
    private DeviceBasicDataService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("deviceBasicData:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/deviceBasicData/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("deviceBasicData:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/deviceBasicData/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("deviceBasicData:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/deviceBasicData/list");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("deviceBasicData:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "装置基础数据指标") DeviceBasicData param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("deviceBasicData:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "装置基础数据指标") DeviceBasicData param,
            @ApiParam(value = "原名称") String originalName) {
        return service.update(param, originalName);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("deviceBasicData:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        return service.deleteByIds(ids);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public DeviceBasicData findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findDeviceBasicDatas")
    @RequiresPermissions("deviceBasicData:list")
    public PageVO<DeviceBasicData> findDeviceBasicDatas(
            @ApiParam(value = "站编码") String stationSystemCode,
            @ApiParam(value = "装置类型编码") String deviceTypeCode,
            @ApiParam(value = "评价指标名称") String evaluationIndexName,
            @ApiParam(value = "评价指标级别") String evaluationIndexLevel,
            @ApiParam(value = "权重") String weights,
            @ApiParam(value = "装置分类") String deviceCategory,
            @ApiParam(value = "页码") int page,
            @ApiParam(value = "每页显示条数") int rows) {
        Page<DeviceBasicData> pageData = service.findDeviceBasicDatas(stationSystemCode, deviceTypeCode, evaluationIndexName, evaluationIndexLevel, weights, deviceCategory, page, rows);
        PageVO<DeviceBasicData> pageVo = new PageVO<>(pageData);
        return pageVo;
    }
}
