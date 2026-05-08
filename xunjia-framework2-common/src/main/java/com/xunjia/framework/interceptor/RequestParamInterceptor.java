package com.xunjia.framework.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.xunjia.framework.utils.StringUtils;

/**
 * URL参数拦截器
 * @author 姜浩
 */
public class RequestParamInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		
		String menuId = request.getParameter("menuId");
		if (!StringUtils.isEmpty(menuId)) {
			request.setAttribute("menuId", menuId);
		}
		
		String theme = request.getParameter("theme");
		if (!StringUtils.isEmpty(theme)) {
			request.setAttribute("theme", theme);
		}
		return true;
	}
}
