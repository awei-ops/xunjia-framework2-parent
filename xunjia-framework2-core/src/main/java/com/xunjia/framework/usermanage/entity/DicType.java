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

/**
 * 字典类型实体
 * 2020年5月8日
 * @author 姜浩
 */
@ApiModel(value="字典类型")
@Entity
@Table(name="f_dic_type")
@Getter
@Setter
public class DicType implements Serializable {

	private static final long serialVersionUID = -771030058579496370L;

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@ApiModelProperty(value="分类名称")
	@Column
	private String name;
	
	@ApiModelProperty(value="名称拼音码")
	@Column
	private String pyCode;
	
	@ApiModelProperty(value="分类编码")
	@Column
	private String code;
	
	@ApiModelProperty(value="排序号",example="1")
	@Column
	private int orderNo;
}
