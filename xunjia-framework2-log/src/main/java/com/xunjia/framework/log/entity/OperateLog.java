package com.xunjia.framework.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志
 * 2023年1月5日
 * @author 姜浩
 */
@ApiModel(value="操作日志")
@Entity
@Table(name="f_operate_log")
@Data
public class OperateLog implements Serializable {

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="用户名")
    @Column
    private String username;

    @ApiModelProperty(value="用户姓名")
    @Column
    private String realName;

    @ApiModelProperty(value="操作ip")
    @Column
    private String ip;

    @ApiModelProperty(value="操作时间")
    @Column
    private Date operateTime;

    @ApiModelProperty(value="模块/菜单")
    @Column
    private String module;

    @ApiModelProperty(value="操作描述")
    @Column
    private String description;

    @ApiModelProperty(value="参数")
    @Lob
    @Column
    private String params;
}
