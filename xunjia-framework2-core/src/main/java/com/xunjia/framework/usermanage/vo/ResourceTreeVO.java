package com.xunjia.framework.usermanage.vo;

import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.utils.StringUtils;

public class ResourceTreeVO extends TreeVO {

	public ResourceTreeVO(Resource resource) {
		super(resource.getId(), resource.getName(), TreeVO.CLOSED, 
				StringUtils.isEmpty(resource.getFontIcon()) ? resource.getImgIcon() : resource.getFontIcon());
	}
	
	public ResourceTreeVO(Resource resource, String state) {
		super(resource.getId(), resource.getName(), state,
				StringUtils.isEmpty(resource.getFontIcon()) ? resource.getImgIcon() : resource.getFontIcon());
	}
}
