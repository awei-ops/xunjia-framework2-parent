package com.xunjia.framework.common.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.interceptor.HttpResponseInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.xunjia.framework.interceptor.RequestParamInterceptor;

import lombok.Getter;

/**
 * 系统自定义配置
 * 2020/5/8
 * @author 姜浩
 */
@Configuration
public class CustomerConfig implements WebMvcConfigurer {

	/** 文件上传目录 */
	@Value("${com.xunjia.framework.baseUploadFolder}")
	@Getter
	private String uploadFolder;

	@Value("${com.xunjia.framework.baseDownloadFolder}")
	@Getter
	private String downloadFolder;
	
	/** 文件访问路径，是文件保存目录的相对路径 */
	@Value("${com.xunjia.framework.uploadAccessPath}")
	@Getter
	private String uploadAccessPath;

	@Value("${com.xunjia.framework.downloadAccessPath}")
	@Getter
	private String downloadAccessPath;
	
	@Value("${com.alibaba.druid.servlet.allow}")
	private String druidAllow;
	
	@Value("${com.alibaba.druid.servlet.deny}")
	private String druidDeny;
	
	/** 配置拦截器 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//通用URL参数拦截器
		registry.addInterceptor(new RequestParamInterceptor())
			.excludePathPatterns("/", "/error", "/doLogin", "/initUser", "/submitInitUser", "/global/**", "/page/**", "/web/**")
			.addPathPatterns("/**");

		//通用HTTP响应拦截器
		registry.addInterceptor(new HttpResponseInterceptor())
				.excludePathPatterns("/", "/error", "/doLogin", "/global/**", "/page/**", "/web/**")
				.addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(uploadAccessPath + "**").addResourceLocations("file:" + uploadFolder);
		registry.addResourceHandler(downloadAccessPath + "**").addResourceLocations("file:" + downloadFolder);
	}
	
	/**
	 * 解决IE浏览器  @ResponseBody返回json的时候提示下载问题
	 * @return
	 */
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        MediaType media = new MediaType(MediaType.TEXT_HTML, Charset.forName("UTF-8"));
        supportedMediaTypes.add(media);
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
        return jsonConverter;
    }
	
	@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
    }
	
	@Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet(){
        //创建servlet注册实体
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>(new StatViewServlet(),"/druid/*");
        //设置ip白名单
		/*
		 * if (!StringUtils.isEmpty(this.druidAllow)) {
		 * servletRegistrationBean.addInitParameter("allow", this.druidAllow); }
		 */
        //设置ip黑名单，如果allow与deny共同存在时,deny优先于allow
		/*
		 * if (!StringUtils.isEmpty(this.druidDeny)) {
		 * servletRegistrationBean.addInitParameter("deny", this.druidDeny); }
		 */
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername","");
        servletRegistrationBean.addInitParameter("loginPassword","");
        //是否可以重置数据
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }
	
	@Bean
    public FilterRegistrationBean<WebStatFilter> statFilter(){
        //创建过滤器
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<WebStatFilter>(new WebStatFilter());
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        //忽略过滤的形式
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
