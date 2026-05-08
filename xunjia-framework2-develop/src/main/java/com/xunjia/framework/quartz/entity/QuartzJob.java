package com.xunjia.framework.quartz.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name="f_quartz_job")
@Data
public class QuartzJob implements Serializable {

	private static final long serialVersionUID = 6256766436270041259L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	private String id;
	
	@Column
	private String jobName;
	
	@Column
	private String jobGroup;
	
	@Column
	private Date createTime;
	
	@Column
	private String cronExpression;
	
	@Column
	private String jobClassName;

	@Column
	private String triggerSalt;

	/**
	 * 任务在调度引擎中的状态
	 * 不持久化到数据库，仅用于显示
	 */
	@Transient
	private String runtimeState;

	@Column
	private int started;
}
