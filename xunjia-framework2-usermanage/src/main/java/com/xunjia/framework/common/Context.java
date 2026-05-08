package com.xunjia.framework.common;

import java.util.List;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.usermanage.entity.UserSettings;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

/**
 * 系统上下文，供开发者随时取得当前登录用户信息、用户权限信息、用户偏好设置
 *
 * @author 姜浩
 * @date 2020年6月20日
 */
public class Context {

    public static UserSettings getCurrentUserSettings() {
        return (UserSettings) getSession().getAttribute("currentUserSettings");
    }

    public static void setCurrentUserSettings(UserSettings userSettings) {
        getSession().setAttribute("currentUserSettings", userSettings);
    }

    public static User getCurrentUser() {
        return (User) getSession().getAttribute("currentUser");
    }

    public static void setCurrentUser(User currentUser) {
        getSession().setAttribute("currentUser", currentUser);
    }

    public static List<Resource> getAuthorizedResources() {
        Session session = getSession();
        return (List<Resource>) session.getAttribute("authorizedResources");
    }

    public static void setAuthorizedResources(List<Resource> authorizedResources) {
        Session session = getSession();
        session.setAttribute("authorizedResources", authorizedResources);
    }

    private static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }
}
