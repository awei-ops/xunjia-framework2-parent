package com.xunjia.framework.usermanage.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="记录事件")
@Entity
@Table(name="logging_event")
@Data
public class LoggingEvent {

	@ApiModelProperty(value="主键id",example="1")
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id", nullable = false)
    private long eventId;

	@ApiModelProperty(value="事件发生时间戳")
    @Column(name = "timestmp", nullable = false)
    private long timestmp;

	@ApiModelProperty(value="事件信息")
    @Column(name = "formatted_message", nullable = false)
    private String formattedMessage;
 

	@ApiModelProperty(value="记录器名称")
    @Column(name = "logger_name", nullable = false)
    private String loggerName;
 
	@ApiModelProperty(value="事件级别")
    @Column(name = "level_string", nullable = false)
    private String levelString;

	@ApiModelProperty(value="线程名称")
    @Column(name = "thread_name")
    private String threadName;
 
    @Column(name = "reference_flag")
    private Integer referenceFlag;
 
    @Column(name = "arg0")
    private String arg0;
 
    @Column(name = "arg1")
    private String arg1;
 
    @Column(name = "arg2")
    private String arg2;
 
    @Column(name = "arg3")
    private String arg3;

	@ApiModelProperty(value="源代码文件名")
    @Column(name = "caller_filename", nullable = false)
    private String callerFilename;
 
	@ApiModelProperty(value="类名")
    @Column(name = "caller_class", nullable = false)
    private String callerClass;
 
	@ApiModelProperty(value="方法名")
    @Column(name = "caller_method", nullable = false)
    private String callerMethod;

	@ApiModelProperty(value="行数")
    @Column(name = "caller_line", nullable = false)
    private String callerLine;
}
