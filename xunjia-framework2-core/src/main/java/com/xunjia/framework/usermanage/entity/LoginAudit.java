package com.xunjia.framework.usermanage.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录审计
 * @author 姜浩
 */
@ApiModel(value="登录审计")
@Entity
@Table(name="f_login_audit")
@Setter
@Getter
@NoArgsConstructor
public class LoginAudit {

	public LoginAudit(String username, String ip, Date loginTime, String from){
		this.username = username;
		this.ip = ip;
		this.loginTime = loginTime;
		this.from = from;
	}

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="登录用户名")
	@Column
	private String username;
	
	@ApiModelProperty(value="登录时间")
	@Column
	private Date loginTime;

	@ApiModelProperty(value="登录ip")
	@Column
	private String ip;

	@ApiModelProperty(value="登录结果，1为成功，0为失败",example = "0")
	@Column
	private int result;

	@ApiModelProperty(value = "登录来源")
	@Column(name="loginFrom")
	private String from;
}
