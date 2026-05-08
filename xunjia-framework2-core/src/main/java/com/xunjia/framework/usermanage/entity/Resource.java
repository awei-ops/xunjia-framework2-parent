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

@ApiModel(value="系统资源")
@Entity
@Table(name="f_resource")
@Getter
@Setter
public class Resource implements Serializable {
	
	private static final long serialVersionUID = -6330571679210832295L;

	public Resource() {}
	
	public Resource(String id) { this.id = id; }

	@ApiModelProperty(value="主键id")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@ApiModelProperty(value="资源名称")
	@Column
	private String name;
	
	@ApiModelProperty(value="资源编码")
	@Column
	private String code;
	
	@ApiModelProperty(value="资源类型")
	@Column
	private String type;
	
	@ApiModelProperty(value="链接地址")
	@Column
	private String url;
	
	@ApiModelProperty(value="字符图标")
	@Column
	private String fontIcon;
	
	@ApiModelProperty(value="图形图标")
	@Column
	private String imgIcon;
	
	@ApiModelProperty(value="资源样式")
	@Column
	private String style;
	
	@ApiModelProperty(value="点击事件")
	@Column
	private String onclick;
	
	@ApiModelProperty(value="排序号",example="1")
	@Column
	private int orderNo;
	
	@ApiModelProperty(value="集成方式")
	@Column
	private String integrateType;
	
	@ApiModelProperty(value="权限码",example = "user:save")
	@Column
	private String permissionCode;
	
	@ApiModelProperty(value="资源是否允许分级授权",example = "1")
	@Column
	private int allowGrant;
	
	@ApiModelProperty(value="可用状态",example="0")
	@Column
	private int enable;
	
	@ApiModelProperty(value="上级资源")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
	private Resource parent;

	@ApiModelProperty(value = "资源大类")
	@Column
	private String category;
}
