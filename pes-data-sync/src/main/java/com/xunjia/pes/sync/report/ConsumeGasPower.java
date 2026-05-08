package com.xunjia.pes.sync.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ConsumeGasPower {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //耗气月报第六作业区集气站耗气
    public List<Map<String,Object>> getJqzSum(String startDate,String endDate){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT JQZMC, SUM(RHQL) AS YHQL FROM A5ADMIN.DMGC_Q_D_QJJQZRSJ ");
        sb.append("WHERE JQZMC IN ('三-1集气站', '三-2集气站', '五站集气站', '涝洲集气站', '庄深1集气站') ");
        sb.append("AND RQ >= TO_DATE('");
        sb.append(startDate);
        sb.append("','yyyy-MM-dd') AND RQ <=TO_DATE('");
        sb.append(endDate);
        sb.append("','yyyy-MM-dd') ");
        sb.append("GROUP BY JQZMC");
        String readDataSql = sb.toString();
        List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);
        return readDataList;
    }

    //获取首页气井产气量
    public List<Map<String,Object>> getQjcqSum(Integer year){
        String startDate = year+"-1-1";
        String queryEnd = year+"-12-31";
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT SUM(RCQL) AS RCQL FROM A5ADMIN.DMGC_Q_D_CQJRSJ ");
        sb.append("WHERE RQ >= TO_DATE('");
        sb.append(startDate);
        sb.append("','yyyy-MM-dd') AND RQ <=TO_DATE('");
        sb.append(queryEnd);
        sb.append("','yyyy-MM-dd')");
        String readDataSql = sb.toString();
        List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);
        return readDataList;
    }

}
