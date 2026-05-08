package com.xunjia.pes.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(value = {"com.xunjia.pes.**.mapper"}, sqlSessionFactoryRef = "pesSqlSessionFactory")
@EnableTransactionManagement
public class PesMybatisConfig {

    @Primary
    @Bean(name = "pesDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pes")
    public DataSource pesDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "pesSqlSessionFactory")
    public SqlSessionFactory pesSqlSessionFactoryBean() throws Exception {

        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean ();
        sqlSessionFactoryBean.setDataSource(pesDataSource());
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager pesTransactionManager() {
        return new DataSourceTransactionManager(pesDataSource());
    }

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}