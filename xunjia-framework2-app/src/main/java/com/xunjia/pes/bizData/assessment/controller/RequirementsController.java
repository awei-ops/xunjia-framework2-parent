package com.xunjia.pes.bizData.assessment.controller;

import com.xunjia.pes.bizData.assessment.entity.Requirements;
import com.xunjia.pes.bizData.assessment.service.RequirementsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/requirements")
public class RequirementsController {
    @Autowired
    private RequirementsService service;

    @ApiOperation(value = "根据类型返指标要求名称或标杆名称", httpMethod = "GET")
    @RequestMapping("/findAllByType")
    public List<Requirements> findAllByType(String type){
        return service.findAllByType(type);
    }
}
