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

@ApiModel(value="资源权限分配")
@Table(name="f_resource_permission")
@Entity
@Getter
@Setter
public class ResourcePermission implements Serializable {

	private static final long serialVersionUID = -381644262060649325L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@ApiModelProperty(value="权限所有者id",example="1")
	@Column
	private String ownerId;
	
	@ApiModelProperty(value="权限所有者类型，R为角色，U为用户")
	@Column
	private String ownerType;
	
	@ApiModelProperty(value="有权资源")
	@Column
	private String resourceId;
}
