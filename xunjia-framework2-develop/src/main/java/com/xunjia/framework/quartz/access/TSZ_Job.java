package com.xunjia.framework.quartz.access;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class TSZ_Job implements Job {

    @Autowired
    private RestTemplate template;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final String url = "http://127.0.0.1:8090/tsz_jxpj/update_Station_pj";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        template.exchange(url, HttpMethod.GET, requestEntity,Boolean.class);
    }
}
