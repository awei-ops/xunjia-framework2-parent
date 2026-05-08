package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.WorkAreaIndicatorSystem;
import com.xunjia.pes.basicDataManage.service.WorkAreaIndicatorSystemService;
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

@Api(value = "作业区指标体系控制器")
@RestController
@RequestMapping("/workAreaIndicatorSystem")
public class WorkAreaIndicatorSystemController {

    @Autowired
    private WorkAreaIndicatorSystemService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("workAreaIndicatorSystem:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/workAreaIndicatorSystem/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("workAreaIndicatorSystem:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/workAreaIndicatorSystem/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("workAreaIndicatorSystem:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/workAreaIndicatorSystem/list");
    }

    @ApiOperation(value = "保存作业区指标体系信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("workAreaIndicatorSystem:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "作业区指标体系信息") WorkAreaIndicatorSystem param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新作业区指标体系信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("workAreaIndicatorSystem:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "作业区指标体系信息") WorkAreaIndicatorSystem param,
            @ApiParam(value = "作业区指标体系原名称") String originalName) {
        return service.update(param,originalName);
    }

    @ApiOperation(value = "批量删除作业区指标体系信息", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("workAreaIndicatorSystem:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "作业区指标体系id数组") @RequestParam(name = "ids[]") String[] ids) {
        return service.deleteByIds(ids);
    }

    @ApiOperation(value = "根据给定id查询批量作业区指标体系信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public WorkAreaIndicatorSystem findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询作业区指标体系分页数据", httpMethod = "GET")
    @RequestMapping("/findWorkAreaIndicatorSystems")
    @RequiresPermissions("workAreaIndicatorSystem:list")
    public PageVO<WorkAreaIndicatorSystem> findWorkAreaIndicatorSystems(
            @ApiParam(value = "作业区编码") String workAreaCode,
            @ApiParam(value = "作业区名称") String workAreaName,
            @ApiParam(value = "评价指标名称") String evaluationIndexName,
            @ApiParam(value = "评价指标级别") String evaluationIndexLevel,
            @ApiParam(value = "权重") String weights,
            @ApiParam(value = "页码") int page,
            @ApiParam(value = "每页显示条数") int rows) {
        Page<WorkAreaIndicatorSystem> pageData = service.findWorkAreaIndicatorSystems(workAreaCode, workAreaName, evaluationIndexName, evaluationIndexLevel, weights, page, rows);
        PageVO<WorkAreaIndicatorSystem> pageVo = new PageVO<>(pageData);
        return pageVo;
    }
}
