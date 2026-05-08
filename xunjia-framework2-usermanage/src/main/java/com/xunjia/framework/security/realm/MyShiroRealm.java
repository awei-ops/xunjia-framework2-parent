package com.xunjia.framework.security.realm;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.resourcePermission.util.AuthorizedResourceUtil;
import com.xunjia.framework.security.exception.PasswordExpiredException;
import com.xunjia.framework.user.repository.IUserRepository;
import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.utils.MD5Pwd;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyShiroRealm extends AuthorizingRealm {

	@Autowired
    private IUserRepository userRepo;
	
	@Autowired
	private AuthorizedResourceUtil authorizedResourceUtil;
	
	@Getter
	@Setter
	private SimpleAuthorizationInfo info = null;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		List<Resource> authorizedResources = Context.getAuthorizedResources();
		
		if (info == null) {
			Set<String> stringPermissions = this.getStringPermissions(Context.getAuthorizedResources());
	        info = new SimpleAuthorizationInfo();
	        info.setStringPermissions(stringPermissions);
		}
        return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		String username = (String) authenticationToken.getPrincipal();
		String password = new String((char[]) authenticationToken.getCredentials());

		// 根据用户名从数据库获取密码
		User user = userRepo.findByUsername(username);
		
		if (user == null || user.getDeleteFlag() == 1) {
			throw new UnknownAccountException("用户名或密码不正确。");
		} else if (!user.getPassword().equals(password)) {
			throw new IncorrectCredentialsException("用户名或密码不正确");
		} else if (user.getEnable() == 0) {
			throw new LockedAccountException("该账户已被禁用。");
		} else if (user.getInitedFlag() == 0){
			throw new DisabledAccountException("该账户未初始化。");
		} else if (user.getPasswordExpireDate() != null && user.getPasswordExpireDate().compareTo(new Date()) < 0){
			throw new PasswordExpiredException("该账户密码已过期。");
		}

		List<Resource> authorizedResources = authorizedResourceUtil.findAuthorizedResources(user.getId(), user.getUsername());

		Context.setAuthorizedResources(authorizedResources);
		Context.setCurrentUser(user);
		Context.setCurrentUserSettings(user.getUserSettings());

		return new SimpleAuthenticationInfo(user, MD5Pwd.MD5Pwd(user.getUsername(), user.getPassword()), ByteSource.Util.bytes(user.getUsername() + MD5Pwd.salt), super.getName());
	}

	private Set<String> getStringPermissions(List<Resource> resources){
		Set<String> stringPermissions = new HashSet<>();
		if (resources != null && resources.size() > 0) {
			for (Resource r : resources) {
				stringPermissions.add(r.getPermissionCode());
			}
		}
		return stringPermissions;
	}
}
