package com.xunjia.framework.usermanage.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value="用户信息")
@Entity
@Table(name="f_user")
@Getter
@Setter
public class User implements Serializable {

	private static final long serialVersionUID = -5934733595201578834L;

	@ApiModelProperty(value="主键id")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;

	@ApiModelProperty(value="用户名")
	@Column
	private String username;

	@ApiModelProperty(value="密码")
	@Column
	private String password;

	@ApiModelProperty(value="真实姓名")
	@Column
	private String realName;

	@ApiModelProperty(value="员工号")
	@Column
	private String staffCode;

	@ApiModelProperty(value="电子邮件")
	@Column
	private String email;

	@ApiModelProperty(value="联系电话")
	@Column
	private String phone;

	@ApiModelProperty(value="身份证号")
	@Column
	private String idCard;

	@ApiModelProperty(value="地址")
	@Column
	private String address;

	@ApiModelProperty(value="用户电子设备编码")
	@Column
	private String eleEquipCode;

	@ApiModelProperty(value="可用状态，0为不可用，1为可用",example = "1")
	@Column
	private int enable;

	@ApiModelProperty(value="用户头像图片")
	@Column
	private String headImage;

	@ApiModelProperty(value="电子签名图片")
	@Column
	private String signImage;

	@ApiModelProperty(value="所属组织机构")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "orgId")
	private Organization org;

	@ApiModelProperty(value="排序号",example = "1")
	@Column
	private int orderNo;

	@ApiModelProperty(value="真实姓名拼音码")
	@Column
	private String realNamePyCode;
	
	@ApiModelProperty(value="当前用户的个性设置")
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userSettingsId",referencedColumnName="id")
	private UserSettings userSettings;

	@ApiModelProperty(value="删除标识")
	@Column
	private int deleteFlag;

	@ApiModelProperty(value="用户是否初始化")
	@Column
	private int initedFlag;

	@ApiModelProperty(value="密码过期时间")
	@Column
	private Date passwordExpireDate;

	@ApiModelProperty(value="微信openid")
	@Column
	private String wechatId;
}
