package com.xunjia.framework.generator.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 自定义实体信息
 * 2020年9月22日
 * @author 姜浩
 */
@Entity
@Table(name="f_custom_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@Column
	private String packageName;
	
	@Column
	private String entityName;
	
	@Column
	private String tableName;
	
	@Column
	private String entityDescr;

	@Column
	private String instruction;
	
	@Column
	private String author;
	
	@Column
	private Date createDate;
	
	@Column
	private int treeStructure;
	
	@Column
	private String operations;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "entity")
	private List<CustomEntityProperty> properties;
}
