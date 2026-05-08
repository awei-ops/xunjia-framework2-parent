package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.CommonEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "通用方法控制器")
@RestController
@RequestMapping("/commonControl")
public class CommonController {
    @RequestMapping("/getYears")
    public List<CommonEntity> getYears() {
        List<CommonEntity> result = new ArrayList<>();
        Date temp = new Date();
        int year = DateUtils.getYear(temp);
        for (int i = year; i >= 2022; i--) {
            CommonEntity commonEntity = new CommonEntity();
            commonEntity.setLabel(String.valueOf(i));
            commonEntity.setValue(String.valueOf(i));
            result.add(commonEntity);
        }
        return result;
    }

    @RequestMapping("/getMonths")
    public List<CommonEntity> getMonths() {
        List<CommonEntity> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            CommonEntity commonEntity = new CommonEntity();
            if (i < 10) {
                commonEntity.setLabel("0" + i);
                commonEntity.setValue("0" + i);
            } else {
                commonEntity.setLabel(String.valueOf(i));
                commonEntity.setValue(String.valueOf(i));
            }
            result.add(commonEntity);
        }
        return result;
    }
}
