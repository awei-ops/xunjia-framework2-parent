package com.xunjia.pes.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class A5JdbcTemplateConfig {

    @Bean(name = "a5DataSource")
    @Qualifier("a5DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.a5")
    public DataSource a5DataSource() {
        return new DruidDataSource();
    }


    @Bean(name = "a5JdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("a5DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}