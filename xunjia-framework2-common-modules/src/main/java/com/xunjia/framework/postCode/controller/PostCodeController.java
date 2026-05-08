package com.xunjia.framework.postCode.controller;

import com.xunjia.framework.postCode.service.PostCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("行政区与编码控制器")
@RestController
@RequestMapping("/postCode")
public class PostCodeController {

    @Autowired
    private PostCodeService service;

    @ApiOperation("查询省份名称列表")
    @GetMapping("/findProvinces")
    public String[] findProvinces(){
        return service.findProvinces();
    }

    @ApiOperation("根据省份名称查询城市名称列表")
    @GetMapping("/findCities")
    public String[] findCities(String province){
        return service.findCities(province);
    }

    @ApiOperation("根据城市名称查询县区列表")
    @GetMapping("/findAreas")
    public String[] findAreas(String city){
        return service.findAreas(city);
    }
}
