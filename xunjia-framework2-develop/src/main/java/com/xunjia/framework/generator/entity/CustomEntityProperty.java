package com.xunjia.framework.generator.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 自定义实体属性
 * 2020年9月22日
 * @author 姜浩
 */
@Entity
@Table(name="f_custom_entity_property")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomEntityProperty {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@Column
	private String propName;
	
	@Column
	private String columnName;
	
	@Column
	private String type;
	
	@Column
	private String propDescr;
	
	@Column
	private int pkFlag;
	
	@Column
	private int uniqueFlag;
	
	@Column
	private int requiredFlag;
	
	@Column
	private int searchFlag;
	
	@Column
	private int enableFlag;
	
	@Column
	private int dicFlag;
	
	@Column
	private int treeNodeFlag;
	
	@Column
	private String dicTypeCode;
	
	@Column
	private int tableColumnFlag;
	
	@Column
	private String searchCond;
	
	@Column
	private String controlType;
	
	@Column
	private String fetchType;
	
	@Column
	private String mappedProp;
	
	@Column
	private String orderBy;
	
	@Column
	private int orderNo;
	
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name = "entityId")
	private CustomEntity entity;
}
