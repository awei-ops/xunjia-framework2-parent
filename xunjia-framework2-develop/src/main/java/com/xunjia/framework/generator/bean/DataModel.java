package com.xunjia.framework.generator.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.ArrayUtils;

import com.alibaba.druid.util.StringUtils;
import com.xunjia.framework.generator.entity.CustomEntity;
import com.xunjia.framework.generator.entity.CustomEntityProperty;
import com.xunjia.framework.utils.ListUtils;

import lombok.Getter;

@Getter
public class DataModel {
	
	public DataModel(CustomEntity entity) {
		List<CustomEntityProperty> properties = entity.getProperties();
		ListUtils.sort(properties, true, "orderNo");
		entity.setProperties(properties);
		this.entity = entity;
		this.classes = new HashSet<>();
		this.methodCode = new HashMap<>();
		this.operations = entity.getOperations() != null ? entity.getOperations().split(",") : new String[0];
		
		if (ArrayUtils.contains(this.operations, "启用") || ArrayUtils.contains(this.operations, "禁用")) {
			this.enableFlag = 1;
		}
		if (ArrayUtils.contains(this.operations, "添加") || ArrayUtils.contains(this.operations, "编辑")) {
			this.submitFlag = 1;
		}
		if (properties.stream().anyMatch(c -> c.getControlType().equals("单选"))){
			this.radioFlag = 1;
		}
		if (properties.stream().anyMatch(c -> c.getControlType().equals("复选"))){
			this.checkboxFlag = 1;
		}
		if (properties.stream().anyMatch(c -> c.getSearchFlag() > 0)){
			this.searchFlag = 1;
		}
		if (properties.stream().anyMatch(c -> com.xunjia.framework.utils.StringUtils.isNotEmpty(c.getOrderBy()))){
			this.orderExist = 1;
		}
		if (properties.stream().anyMatch(c -> c.getControlType().equals("用户选择"))){
			this.selectUserFlag = 1;
		}
		if (properties.stream().anyMatch(c -> c.getControlType().equals("组织选择"))){
			this.selectOrgFlag = 1;
		}


		//default classes
		this.classes.add("org.slf4j.Logger");
		this.classes.add("org.slf4j.LoggerFactory");
		this.classes.add("java.io.Serializable");
		this.classes.add("javax.persistence.CascadeType");
		this.classes.add("javax.persistence.Column");
		this.classes.add("javax.persistence.Entity");
		this.classes.add("javax.persistence.FetchType");
		this.classes.add("javax.persistence.GeneratedValue");
		this.classes.add("javax.persistence.Id");
		this.classes.add("javax.persistence.Table");
		this.classes.add("org.hibernate.annotations.GenericGenerator");
		this.classes.add("lombok.AllArgsConstructor");
		this.classes.add("lombok.Getter");
		this.classes.add("lombok.NoArgsConstructor");
		this.classes.add("lombok.Setter");
		
		if (!ListUtils.isListEmpty(properties)) {
			for (CustomEntityProperty prop : properties) {
				String type = prop.getType();
				String columnName = prop.getColumnName();
				if (StringUtils.isEmpty(columnName)) {
					prop.setColumnName(prop.getPropName());
				}
				if (prop.getPropName().equals("parent")) {
					this.parentProp = prop;
				}
				
				if (type.contains("List")) {
					type = type.replace("〈", "<").replace("〉", ">");
					this.classes.add("javax.persistence.OneToMany");
					this.classes.add("java.util.List");
				} else if (type.equals("Date")) {
					this.classes.add("java.util.Date");
					this.classes.add("org.springframework.format.annotation.DateTimeFormat");
				} else if (type.contains(".")) {
					this.classes.add("javax.persistence.JoinColumn");
					this.classes.add("javax.persistence.ManyToOne");
					//this.classes.add(type);
				} 
				
				if (type.contains("当前类型")) {
					this.classes.add("javax.persistence.JoinColumn");
					this.classes.add("javax.persistence.ManyToOne");
					prop.setType(type.replace("当前类型", entity.getEntityName()));
				}
				
				if (prop.getPkFlag() == 1) {
					this.pkProp = prop;
				} else if (prop.getEnableFlag() == 1) {
					this.enableProp = prop;
				}
				
				if (prop.getControlType().equals("文件")) {
					this.encType = "multipart/form-data";
				}
			}
		}
	}

	private CustomEntity entity;
	
	private Set<String> classes;
	
	private String[] operations;
	
	private String encType = "application/x-www-form-urlencoded";
	
	private Map<String, String> methodCode;
	
	private CustomEntityProperty pkProp;
	
	private CustomEntityProperty enableProp;
	
	private CustomEntityProperty parentProp;
	
	/** 是否包含启用/禁用 */
	private int enableFlag;
	
	/** 是否包含保存/更新 */
	private int submitFlag;

	/** 是否包含单选 */
	private int radioFlag;

	/** 是否包含复选 */
	private int checkboxFlag;

	/** 是否包含查询条件 */
	private int searchFlag;

	/** 是否存在排序 */
	private int orderExist;

	/** 是否存在选择用户 */
	private int selectUserFlag;

	/** 是否存在选择组织 */
	private int selectOrgFlag;
}
