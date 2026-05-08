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

/**
 * 字典内容实体
 * 2020年5月8日
 * @author 姜浩
 */
@ApiModel(value="字典内容")
@Entity
@Table(name="f_dic_content")
@Setter
@Getter
public class DicContent implements Serializable {

	private static final long serialVersionUID = -7077469700329523560L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@ApiModelProperty(value="字典内容名称")
	@Column
	private String name;
	
	@ApiModelProperty(value="名称拼音码")
	@Column
	private String pyCode;
	
	@ApiModelProperty(value="内容编码")
	@Column
	private String code;
	
	@ApiModelProperty(value="排序号",example="1")
	@Column
	private int orderNo;
	
	@ApiModelProperty(value="上级字典内容")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "parentContentId")
	private DicContent parentContent;
	
	@ApiModelProperty(value="字典分类")
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId")
	private DicType type;
}
