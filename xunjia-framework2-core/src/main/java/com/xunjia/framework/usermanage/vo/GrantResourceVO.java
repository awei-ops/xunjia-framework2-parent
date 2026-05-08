package com.xunjia.framework.usermanage.vo;

import java.util.List;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.utils.ListUtils;

import lombok.Data;

/**
 * 角色/用户的有权菜单视图模型
 * 2020年6月16日
 * @author 姜浩
 */
@Data
public class GrantResourceVO {
	
	public GrantResourceVO(Resource resource, String rankName, List<Resource> subResources) {
		this.resource = resource;
		this.id = resource.getId();
		this.rankName = rankName;
		
		StringBuffer sb = new StringBuffer();
		if (!ListUtils.isListEmpty(subResources)) {
			for (Resource r : subResources) {
				sb.append(r.getName()).append("(").append(r.getPermissionCode()).append(")").append("<br/>");
			}
			subResourceNames = sb.toString().substring(0, sb.length() - 5);
		}
	}
	
	private String id;
	
	private Resource resource;
	
	private String rankName;
	
	/** 有权按钮 */
	private String subResourceNames;
	
	/**
	 * 资源是否已授权
	 */
	private boolean isAuthorized;
}
