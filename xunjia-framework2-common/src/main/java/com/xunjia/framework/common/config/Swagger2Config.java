package com.xunjia.framework.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置信息
 * 2020/5/8
 * @author 姜浩
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {

	@Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xunjia")) //controller的根目录
                .paths(PathSelectors.any()) //可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build();
    }
 
    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("大庆讯加软件开发平台")
                .description("大庆讯加软件开发平台")
                .version("2.0.0")
                .termsOfServiceUrl("10.114.229.179")
                .build();
    }
}
