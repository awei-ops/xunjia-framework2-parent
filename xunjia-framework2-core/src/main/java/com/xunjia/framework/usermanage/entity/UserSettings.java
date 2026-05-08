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

@ApiModel(value="用户设置")
@Entity
@Table(name="f_user_settings")
@Setter
@Getter
public class UserSettings implements Serializable {

	private static final long serialVersionUID = -3274348978475206491L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@Column
	private String userId;
	
	@ApiModelProperty(value="默认首页")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "defaultMenuId")
	private Resource defaultMenu;
	
	@Column
	private String customTheme;
}
