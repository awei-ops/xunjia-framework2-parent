package com.xunjia.framework.usermanage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="记录异常")
@Data
@Entity
@Table(name="logging_event_exception")
@IdClass(LoggingEventExceptionKey.class)
public class LoggingEventException {

	@ApiModelProperty(value="主键id",example="1")
	@Id
	@Column(name="event_id")
	private long eventId;

	@ApiModelProperty(value="序号")
	@Id
	@Column
	private short i;

	@ApiModelProperty(value="方法栈")
	@Column(name="trace_line")
	private String traceLine;
}
