package com.xunjia.pes.controller.waterTreatment;

import com.xunjia.pes.sync.oil.DMGC_Y_ZYZ_Sync;
import com.xunjia.pes.sync.waterTreatment.DMGC_S_SCLJB_Sync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dmgc_s_scljb_sync")
public class DMGC_S_SCLJB_Sync_Controller {
    @Autowired
    private DMGC_S_SCLJB_Sync service;

    @RequestMapping("/getA5_Data")
    public void sync(String startTime, String endTime) throws IllegalAccessException {
        service.sync(null,null);
    }
}
