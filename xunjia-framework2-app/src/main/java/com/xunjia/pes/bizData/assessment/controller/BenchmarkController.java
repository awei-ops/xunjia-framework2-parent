package com.xunjia.pes.bizData.assessment.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.bizData.assessment.entity.Benchmark;
import com.xunjia.pes.bizData.assessment.service.BenchmarkService;
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
@RequestMapping("/benchmarks")
public class BenchmarkController {
    @Autowired
    private BenchmarkService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("benchmarks:save")
    public ModelAndView toAdd() {
        return new ModelAndView("bizData/assessment/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("benchmarks:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("bizData/assessment/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("benchmarks:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/assessment/list");
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toDisplay")
    public ModelAndView toDisplay() {
        return new ModelAndView("bizData/assessment/displayList");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("benchmarks:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "装置类型") Benchmark param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("benchmarks:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "装置类型") Benchmark param,
            @ApiParam(value = "原编码") String originalCode) {
        return service.update(param, originalCode);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("benchmarks:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public Benchmark findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/findBenchmarks")
    @RequiresPermissions("benchmarks:list")
    public PageVO<Benchmark> findBenchmarks(
            Benchmark example, int page, int rows) {
        example.setDeleteFlag(0);
        PageVO<Benchmark> pageVo = service.getPageData(example, page, rows);
        return pageVo;
    }

    @ApiOperation(value = "查询所有信息", httpMethod = "GET")
    @RequestMapping("/findAll")
    public List<Benchmark> findAllBenchmarks() {
        return service.findAll();
    }

    @ApiOperation(value = "根据code查询", httpMethod = "GET")
    @RequestMapping("/getByCode")
    public Benchmark getByCode(String code){
        return  service.getByCode(code);
    }

    @ApiOperation(value = "通过类型查询考核指标", httpMethod = "GET")
    @RequestMapping("/getBenchmarksByType")
    public List<Benchmark> getBenchmarksByType(String type){
        return service.getBenchmarksByType(type);
    }
}
