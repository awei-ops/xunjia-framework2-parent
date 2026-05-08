package com.xunjia.framework.usermanage.vo;


import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.usermanage.entity.Organization;

public class OrgTreeVO extends TreeVO {

	public OrgTreeVO(Organization org) {
		super(org.getId(), org.getName(), OrgTreeVO.CLOSED, org.getType().getIcon());
	}
	
	public OrgTreeVO(Organization org, String state) {
		super(org.getId(), org.getName(), state, org.getType().getIcon());
	}
	
}
