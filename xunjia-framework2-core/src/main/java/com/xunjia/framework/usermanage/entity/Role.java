package com.xunjia.framework.usermanage.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value="角色信息")
@Table(name="f_role")
@Entity
@Getter
@Setter
public class Role implements Serializable {

	private static final long serialVersionUID = 873638141550514423L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="角色名称")
	@Column
	private String name;

	@ApiModelProperty(value="排序号",example = "1")
	@Column
	private int orderNo;

	@ApiModelProperty(value="可用状态",example = "1")
	@Column
	private int enable;

	@ApiModelProperty(value="名称拼音码")
	@Column
	private String pyCode;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
	@JoinColumn(name="organizationId")
	private Organization organization;
}