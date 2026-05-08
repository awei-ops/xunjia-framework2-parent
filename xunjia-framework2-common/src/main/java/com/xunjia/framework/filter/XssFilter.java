package com.xunjia.framework.filter;

import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.wrapper.XssHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssFilter implements Filter {

    private List<String> excludes = new ArrayList<>();

    private boolean enabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String strExcludes = filterConfig.getInitParameter("excludes");
        String strEnabled = filterConfig.getInitParameter("enabled");
        //将不需要xss过滤的接口添加到列表中
        if(StringUtils.isNotEmpty(strExcludes)){
            String[] urls = strExcludes.split(",");
            Collections.addAll(excludes, urls);
        }
        if(StringUtils.isNotEmpty(strEnabled)){
            enabled = Boolean.parseBoolean(strEnabled);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        //如果该访问接口在排除列表里面则不拦截
        if(isExcludeUrl(servletRequest.getServletPath())){
            chain.doFilter(servletRequest,response);
            return;
        }
        //拦截该url并进行xss过滤
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(servletRequest);
        chain.doFilter(xssHttpServletRequestWrapper, response);
    }

    private boolean isExcludeUrl(String urlPath){
        if(!enabled){
            //如果xss开关关闭了，则所有url都不拦截
            return true;
        }
        if(excludes==null||excludes.isEmpty()){
            return false;
        }

        for(String pattern:excludes){
            Pattern p = Pattern.compile("^"+pattern);
            Matcher m = p.matcher(urlPath);
            if(m.find()){
                return true;
            }
        }
        return false;
    }
}
