package com.xunjia.pes.controller.waterInjection;

import com.xunjia.pes.sync.waterInjection.DMGC_S_ZSZ_Sync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dmgc_s_zsz_sync")
public class DMGC_S_ZSZ_Sync_Controller {
    @Autowired
    private DMGC_S_ZSZ_Sync service;

    @RequestMapping("/getA5_Data")
    public void sync(String startTime, String endTime) throws IllegalAccessException {
        service.sync(null,null);
    }
}
