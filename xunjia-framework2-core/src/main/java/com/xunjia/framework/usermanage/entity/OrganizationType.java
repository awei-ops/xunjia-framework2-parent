package com.xunjia.framework.usermanage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value="组织机构分类")
@Entity
@Table(name="f_organization_type")
@Getter
@Setter
public class OrganizationType implements Serializable {

	private static final long serialVersionUID = -68112738727727606L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="组织类别名称")
	@Column
	private String name;
	
	@ApiModelProperty(value="组织类别图标，是一个css的class名")
	@Column
	private String icon;

	@ApiModelProperty(value="排序号",example = "1")
	@Column
	private int orderNo;
}
