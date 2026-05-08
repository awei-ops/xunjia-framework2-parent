package com.xunjia.pes.controller.oil;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.pes.sync.oil.DMGC_Y_D_JRL_Sync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/dmgc_y_d_jrl_sync")
public class DMGC_Y_D_JRL_Sync_Controller {
    @Autowired
    private DMGC_Y_D_JRL_Sync service;

    @RequestMapping("/getA5_Data")
    public void sync(String startTime, String endTime) throws IllegalAccessException, ParseException {
        service.sync(DateUtils.parse(startTime,DateUtils.DATE_PATTERN),DateUtils.parse(endTime,DateUtils.DATE_PATTERN));
    }
}
