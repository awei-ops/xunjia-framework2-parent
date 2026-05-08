package com.xunjia.framework.usermanage.vo;

import com.xunjia.framework.usermanage.entity.Organization;
import lombok.Data;

/**
 * 组织授权页面视图模型
 * 2020年5月9日
 * @author 姜浩
 */
@Data
public class GrantOrgVO {
	
	public GrantOrgVO() {}
	
	public GrantOrgVO(Organization org, String rankName) {
		this.orgId = org.getId();
		this.orgName = org.getName();
		this.rankName = rankName;
	}
	
	/** 当前组织id */
	private String orgId;
	
	private String orgName;

	/** 当前组织 */
	private String rankName;
	
	/** 当前角色(用户)对组织是否有权 */
	private boolean orgRighted;
	
}
