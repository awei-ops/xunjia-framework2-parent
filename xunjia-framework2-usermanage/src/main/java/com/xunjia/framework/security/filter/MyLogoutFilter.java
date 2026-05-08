package com.xunjia.framework.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.xunjia.framework.security.realm.MyShiroRealm;

public class MyLogoutFilter extends LogoutFilter {

	@Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		//登出操作 清除缓存  subject.logout() 可以自动清理缓存信息, 这些代码是可以省略的  这里只是做个笔记 表示这种方式也可以清除
        //Subject subject = getSubject(request,response);
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        MyShiroRealm shiroRealm = (MyShiroRealm) securityManager.getRealms().iterator().next();
        shiroRealm.setInfo(null);
        //PrincipalCollection principals = subject.getPrincipals();
        //shiroRealm.clearCache(principals);

        //登出
		Subject subject = getSubject(request,response);
        subject.logout();
        
        //session失效
        ((HttpServletRequest)request).getSession().invalidate();

        //获取登出后重定向到的地址
        String redirectUrl = getRedirectUrl(request,response,subject);
        //重定向
        issueRedirect(request,response,redirectUrl);
        return false;
	}
}
