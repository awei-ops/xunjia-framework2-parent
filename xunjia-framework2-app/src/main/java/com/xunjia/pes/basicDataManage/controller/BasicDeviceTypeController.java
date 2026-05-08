package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.BasicDeviceType;
import com.xunjia.pes.basicDataManage.service.BasicDeviceTypeService;
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

import java.util.List;

@Api(value = "装置类型维护控制器")
@RestController
@RequestMapping("/basicDeviceType")
public class BasicDeviceTypeController {
    @Autowired
    private BasicDeviceTypeService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("basicDeviceType:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/basicDeviceType/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("basicDeviceType:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/basicDeviceType/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("basicDeviceType:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/basicDeviceType/list");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("basicDeviceType:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "装置类型") BasicDeviceType param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("basicDeviceType:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "装置类型") BasicDeviceType param,
            @ApiParam(value = "原编码") String originalCode) {
        return service.update(param, originalCode);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("basicDeviceType:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        return service.deleteByIds(ids);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public BasicDeviceType findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findBasicDeviceTypes")
    @RequiresPermissions("basicDeviceType:list")
    public PageVO<BasicDeviceType> findBasicDeviceTypes(
            @ApiParam(value = "装置类型编码") String deviceTypeCode,
            @ApiParam(value = "装置类型名称") String deviceTypeName,
            @ApiParam(value = "装置类型分类") String deviceCategory,
            @ApiParam(value = "页码") int page,
            @ApiParam(value = "每页显示条数") int rows) {
        Page<BasicDeviceType> pageData = service.findBasicDeviceTypes(deviceTypeCode, deviceTypeName, deviceCategory, page, rows);
        PageVO<BasicDeviceType> pageVo = new PageVO<>(pageData);
        return pageVo;
    }

    @ApiOperation(value = "根据给定类别查询信息", httpMethod = "GET")
    @RequestMapping("/findAllBasicDeviceTypes")
    public List<BasicDeviceType> findAllBasicDeviceTypes(@ApiParam(value = "装置类型分类") String deviceCategory) {
        return service.findAllBasicDeviceTypes(deviceCategory);
    }
}
