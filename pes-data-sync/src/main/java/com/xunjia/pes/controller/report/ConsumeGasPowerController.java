package com.xunjia.pes.controller.report;

import com.xunjia.pes.sync.report.ConsumeGasPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/a5_consume_gas")
public class ConsumeGasPowerController {
    @Autowired
    private ConsumeGasPower service;

    @RequestMapping("/getJqzSum")
    public List<Map<String,Object>> getJqzSum(String startDate, String endDate){
        return service.getJqzSum(startDate,endDate);
    }

    @RequestMapping("/getQjcqSum")
    public List<Map<String,Object>> getQjcqSum(Integer year){
        return service.getQjcqSum(year);
    }
}
