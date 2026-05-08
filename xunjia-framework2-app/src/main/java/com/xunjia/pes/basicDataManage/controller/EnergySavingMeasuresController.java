package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.pes.basicDataManage.entity.EnergySavingMeasures;
import com.xunjia.pes.basicDataManage.service.EnergySavingMeasuresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "节能措施控制器")
@RestController
@RequestMapping("/energySavingMeasures")
public class EnergySavingMeasuresController {
    @Autowired
    private EnergySavingMeasuresService service;
    @ApiOperation(value = "查询数据", httpMethod = "GET")
    @RequestMapping("/getEnergySavingMeasuresByTypeCode")
    public List<EnergySavingMeasures> getEnergySavingMeasuresByTypeCode(String typeCode){
        return service.getEnergySavingMeasuresByTypeCode(typeCode);
    }
}
