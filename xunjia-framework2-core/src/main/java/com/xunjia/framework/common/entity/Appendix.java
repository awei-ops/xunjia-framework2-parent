package com.xunjia.framework.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 姜浩
 * 附件实体
 */
@Entity
@Table(name="appendix")
@Getter
@Setter
public class Appendix {

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	/**
	 * 保存文件夹
	 */
	@Column
	private String dir;
	
	/**
	 * 保存文件名
	 */
	@Column
	private String fileName;
	
	/**
	 * 原文件名
	 */
	@Column
	private String originalFileName;
	
	/**
	 * 文件类型
	 */
	@Column
	private String contentType;
	
	/**
	 * 业务类型
	 */
	@Column
	private String businessType;
	
	/**
	 * 业务
	 */
	@Column
	private String businessId;
	
	/**
	 * 描述
	 */
	@Column
	private String descr;

}
