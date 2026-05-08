package com.xunjia.framework.quartz.config;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

	@Autowired
    private XunjiaJobFactory jobFactory;
	
	/**
     * 指定持久化配置文件
     * @return
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource ds){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setStartupDelay(1);
        schedulerFactoryBean.setDataSource(ds);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
        //schedulerFactoryBean.setConfigLocation(new ClassPathResource("/application.yml"));
        return schedulerFactoryBean;
    }

    /**
     * 创建getScheduler
     * @return
     */
    @Bean
    public Scheduler scheduler(DataSource ds){
        return schedulerFactoryBean(ds).getScheduler();
    }
}
