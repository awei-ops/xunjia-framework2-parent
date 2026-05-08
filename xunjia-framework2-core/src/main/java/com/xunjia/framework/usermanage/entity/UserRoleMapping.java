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

@ApiModel(value="用户角色映射关系")
@Entity
@Table(name="f_user_role_mapping")
@Getter
@Setter
public class UserRoleMapping implements Serializable {

	private static final long serialVersionUID = 8995687514258002963L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="用户")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
	private User user;

	@ApiModelProperty(value="角色")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId")
	private Role role;
}
