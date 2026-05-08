package com.xunjia.pes.controller.oil;

import com.xunjia.pes.sync.oil.DMGC_JRL_Sync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dmgc_jrl_sync")
public class DMGC_JRL_Sync_Controller {
    @Autowired
    private DMGC_JRL_Sync service;

    @RequestMapping("/getA5_Data")
    public void sync(String startTime, String endTime) throws IllegalAccessException {
        service.sync(null,null);
    }
}
