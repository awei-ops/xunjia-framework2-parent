package com.xunjia.framework.usermanage.entity;

import java.io.Serializable;

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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value="组织机构")
@Entity
@Table(name="f_organization")
@Getter
@Setter
public class Organization implements Serializable {

	private static final long serialVersionUID = 8466814976218332298L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="组织机构名称")
	@Column
	private String name;
	
	@ApiModelProperty(value="组织机构代码")
	@Column
	private String code;

	@ApiModelProperty(value="排序号",example = "1")
	@Column
	private int orderNo;

	@ApiModelProperty(value="层级", example = "1")
	@Column(name="org_level")
	private int level;

	@ApiModelProperty(value="可用状态，0为不可用，1为可用",example = "1")
	@Column
	private int enable;

	@ApiModelProperty(value="上级组织机构")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "parentOrgId")
	private Organization parent;

	@ApiModelProperty(value="组织分类")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId")
	private OrganizationType type;

	@ApiModelProperty(value="名称拼音码")
	@Column
	private String pyCode;

	@ApiModelProperty(value="删除标记")
	@Column
	private int deleteFlag;

}
