package com.xunjia.framework.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 任务日志
 * 2023年4月14日
 * @author 姜浩
 */
@ApiModel(value="任务日志")
@Entity
@Table(name="f_quartz_job_log")
@Data
public class QuartzJobLog {

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="任务开始时间")
    @Column
    private Date startTime;

    @ApiModelProperty(value="任务结束时间")
    @Column
    private Date finishTime;

    @ApiModelProperty(value="任务名称")
    @Column
    private String jobName;

    @ApiModelProperty(value="任务实例id")
    @Column
    private String jobInstId;

    @ApiModelProperty(value="执行是否成功")
    @Column
    private Boolean executeResult;

    @ApiModelProperty(value="执行返回信息")
    @Column
    private String executeMsg;

}
