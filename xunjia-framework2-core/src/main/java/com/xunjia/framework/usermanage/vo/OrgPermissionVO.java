package com.xunjia.framework.usermanage.vo;

import lombok.Data;

/**
 * 角色/用户的有权菜单视图模型
 * 2020/5/8
 * @author 姜浩
 */
@Data
public class OrgPermissionVO {
	
	public OrgPermissionVO() { }
	
	public OrgPermissionVO(int id, String orgName, int level, int orderNo) {
		this.id = id;
		this.orgName = orgName;
		this.level = level;
		this.orderNo = orderNo;
	}

	/** 权限记录id */
	private int id;
	
	/** 有权组织名称 */
	private String orgName;
	
	/** 组织层级 */
	private int level;
	
	/** 组织排序号 */
	private int orderNo;
	
}
