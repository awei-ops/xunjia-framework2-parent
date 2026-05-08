package com.xunjia.pes.controller.oil;

import com.xunjia.pes.sync.oil.DMGC_Y_TSZ_NEW_Sync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dmgc_y_tsz_new_sync")
public class DMGC_Y_TSZ_NEW_Sync_Controller {
    @Autowired
    private DMGC_Y_TSZ_NEW_Sync service;

    @RequestMapping("/getA5_Data")
    public void sync(String startTime, String endTime) throws IllegalAccessException {
        service.sync(null,null);
    }
}
