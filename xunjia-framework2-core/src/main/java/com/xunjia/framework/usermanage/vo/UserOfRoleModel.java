package com.xunjia.framework.usermanage.vo;

import java.io.Serializable;

import com.xunjia.framework.usermanage.entity.User;
import lombok.Data;

/**
 * 拥有某个角色的用户信息模型
 * 2020-05-17
 * @author 姜浩
 */
@Data
public class UserOfRoleModel implements Serializable {

	private static final long serialVersionUID = 2330217520744270546L;

	public UserOfRoleModel(User user, String orgName) {
		this.id = user.getId();
		this.user = user;
		this.orgName = orgName;
	}
	
	private String id;
	
	private User user;
	
	private String orgName;
}
