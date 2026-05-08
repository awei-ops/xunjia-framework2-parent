package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.StationSystem;
import com.xunjia.pes.basicDataManage.service.StationSystemService;
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

@Api(value = "转油（放水）站指标体系、脱水站指标体系、注水站指标体系、污水处理站指标体系控制器")
@RestController
@RequestMapping("/stationSystem")
public class StationSystemController {
    @Autowired
    private StationSystemService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("stationSystem:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/stationSystem/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("stationSystem:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/stationSystem/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("stationSystem:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/stationSystem/list");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("stationSystem:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "转油（放水）站指标体系、脱水站指标体系、注水站指标体系、污水处理站指标体系") StationSystem param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("stationSystem:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "转油（放水）站指标体系、脱水站指标体系、注水站指标体系、污水处理站指标体系") StationSystem param,
            @ApiParam(value = "原名称") String originalName) {
        return service.update(param, originalName);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("stationSystem:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        return service.deleteByIds(ids);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public StationSystem findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findStationSystems")
    @RequiresPermissions("stationSystem:list")
    public PageVO<StationSystem> findStationSystems(
            @ApiParam(value = "作业区编码") String professionalSystemCode,
            @ApiParam(value = "专业系统编码") String stationSystemCode,
            @ApiParam(value = "评价指标名称") String evaluationIndexName,
            @ApiParam(value = "评价指标级别") String evaluationIndexLevel,
            @ApiParam(value = "权重") String weights,
            @ApiParam(value = "指标体系类型") String stationType,
            @ApiParam(value = "页码") int page,
            @ApiParam(value = "每页显示条数") int rows) {
        Page<StationSystem> pageData = service.findStationSystems(professionalSystemCode, stationSystemCode, evaluationIndexName, evaluationIndexLevel, weights, stationType, page, rows);
        PageVO<StationSystem> pageVo = new PageVO<>(pageData);
        return pageVo;
    }
}
