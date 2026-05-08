package com.xunjia.framework.resource.html;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.utils.StringUtils;

public class ButtonHtmlGen {

	public static String generateButtonHtml(Resource resource) {
		String btnClass = "easyui-linkbutton";
		if (!StringUtils.isEmpty(resource.getStyle())) {
			btnClass += " " + resource.getStyle();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("<a").append(" href='javascript:'").append(" class='").append(btnClass).append("'");
		if (!StringUtils.isEmpty(resource.getOnclick())) {
			sb.append(" onclick='").append(resource.getOnclick()).append("'");
		}
		String btnIcon = StringUtils.isEmpty(resource.getFontIcon()) ? resource.getImgIcon() : resource.getFontIcon();
		if (!StringUtils.isEmpty(btnIcon)) {
			sb.append(" iconCls='").append(btnIcon).append("'");
		}
		sb.append(">").append(resource.getName()).append("</a>");
		return sb.toString();
	}
}
