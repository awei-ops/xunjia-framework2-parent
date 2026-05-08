package com.xunjia.framework.quartz.initial;

import com.xunjia.framework.quartz.service.QuartzJobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartJobAuto {

    @Autowired
    private QuartzJobService service;

    @PostConstruct
    public void startJobAuto(){
        try {
            service.autoStartJobs();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
