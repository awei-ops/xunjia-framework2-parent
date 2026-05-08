package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.ProfessionalSystem;
import com.xunjia.pes.basicDataManage.service.ProfessionalSystemService;
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

@Api(value = "原油集输系统指标体系、注入系统指标体系、水处理系统指标体系控制器")
@RestController
@RequestMapping("/professionalSystem")
public class ProfessionalSystemController {
    @Autowired
    private ProfessionalSystemService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("professionalSystem:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/professionalSystem/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("professionalSystem:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/professionalSystem/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("professionalSystem:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/professionalSystem/list");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("professionalSystem:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "原油集输系统指标体系、注入系统指标体系、水处理系统指标体系信息") ProfessionalSystem param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("professionalSystem:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "原油集输系统指标体系、注入系统指标体系、水处理系统指标体系信息") ProfessionalSystem param,
            @ApiParam(value = "原名称") String originalName) {
        return service.update(param, originalName);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("professionalSystem:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        return service.deleteByIds(ids);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public ProfessionalSystem findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findProfessionalSystems")
    @RequiresPermissions("professionalSystem:list")
    public PageVO<ProfessionalSystem> findProfessionalSystems(
            @ApiParam(value = "作业区编码") String workAreaCode,
            @ApiParam(value = "专业系统编码") String professionalSystemCode,
            @ApiParam(value = "评价指标名称") String evaluationIndexName,
            @ApiParam(value = "评价指标级别") String evaluationIndexLevel,
            @ApiParam(value = "权重") String weights,
            @ApiParam(value = "指标体系类型") String professionalType,
            @ApiParam(value = "页码") int page,
            @ApiParam(value = "每页显示条数") int rows) {
        Page<ProfessionalSystem> pageData = service.findProfessionalSystems(workAreaCode, professionalSystemCode, evaluationIndexName, evaluationIndexLevel, weights, professionalType, page, rows);
        PageVO<ProfessionalSystem> pageVo = new PageVO<>(pageData);
        return pageVo;
    }
}
