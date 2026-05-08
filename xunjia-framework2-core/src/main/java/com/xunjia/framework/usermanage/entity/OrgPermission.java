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

@ApiModel(value="组织机构权限")
@Entity
@Table(name="f_org_permission")
@Getter
@Setter
public class OrgPermission implements Serializable {

	private static final long serialVersionUID = 7414840024725255894L;

	@ApiModelProperty(value="主键id")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="拥有者ID",example = "1")
	@Column
	private String ownerId;

	@ApiModelProperty(value="拥有者分类，R为角色，U为用户")
	@Column
	private String ownerType;

	@ApiModelProperty(value="有权组织")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId")
	private Organization org;
}
