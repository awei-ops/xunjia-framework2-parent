package com.xunjia.framework.quartz.test;

import com.xunjia.framework.utils.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.xunjia.framework.quartz.service.QuartzJobService;

import java.util.Date;

public class TestJob implements Job {

	@Autowired
	private QuartzJobService service;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			System.out.println("当前时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.sss"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
