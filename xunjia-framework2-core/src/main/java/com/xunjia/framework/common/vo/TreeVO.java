package com.xunjia.framework.common.vo;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * easyui树节点视图对象
 * 2020/5/8
 * @author 姜浩
 */
@Getter
@Setter
public class TreeVO {
	
	/** 节点开启状态 */
	public static final String OPEN = "open";
	
	/** 节点关闭状态 */
	public static final String CLOSED = "closed";
	
	public TreeVO(int id, String text, String state, String iconCls) {
		this.id = String.valueOf(id);
		this.text = text;
		this.state = state;
		this.iconCls = iconCls;
	}
	
	public TreeVO(String id, String text, String state, String iconCls) {
		this.id = id;
		this.text = text;
		this.state = state;
		this.iconCls = iconCls;
	}

	/** 节点id */
	private String id;
	
	/** 节点名称 */
	private String text;
	
	/** 节点展开状态 */
	private String state;
	
	/** 节点图标 */
	private String iconCls;
	
	/** 节点checkbox是否被选中 */
	private boolean checked;
	
	/** 节点附加属性 */
	private Map<String, Object> attributes;
	
	/** 子节点 */
	private List<TreeVO> children;
}
