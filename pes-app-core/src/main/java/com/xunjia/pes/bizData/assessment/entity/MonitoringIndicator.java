package com.xunjia.pes.bizData.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监测项目与指标要求")
@TableName("t_monitoring")
@Data
public class MonitoringIndicator {
    @ApiModelProperty(value = "id")
    @TableId
    private String id;
    @ApiModelProperty("类别")
    @TableField
    private String type;
    @ApiModelProperty("额定排量上限（Q<）")
    @TableField
    private Double ratedDischargeMax;
    @ApiModelProperty("额定排量下限（Q>=）")
    @TableField
    private Double ratedDischargeMin;
    @ApiModelProperty("监测项目1")
    @TableField
    private String monitoringItemOne;
    @ApiModelProperty("监测项目1限定值")
    @TableField
    private Double monitoringItemOneLimit;
    @ApiModelProperty("监测项目1节能值")
    @TableField
    private Double monitoringItemOneEnergy;
    @ApiModelProperty("监测项目1最低评价")
    @TableField
    private String itemOneMinEvaluation;
    @ApiModelProperty("监测项目1中等评价")
    @TableField
    private String itemOneMidEvaluation;
    @ApiModelProperty("监测项目1最高评价")
    @TableField
    private String itemOneMaxEvaluation;
    @ApiModelProperty("监测项目2")
    @TableField
    private String monitoringItemTwo;
    @ApiModelProperty("监测项目2限定值")
    @TableField
    private Double monitoringItemTwoLimit;
    @ApiModelProperty("监测项目2节能值")
    @TableField
    private Double monitoringItemTwoEnergy;
    @ApiModelProperty("监测项目3")
    @TableField
    private String monitoringItemThree;
    @ApiModelProperty("监测项目3限定值")
    @TableField
    private Double monitoringItemThreeLimit;
    @ApiModelProperty("监测项目3最低评价")
    @TableField
    private String itemThreeMinEvaluation;
    @ApiModelProperty("监测项目3中等评价")
    @TableField
    private String itemThreeMidEvaluation;
    @ApiModelProperty("监测项目3最高评价")
    @TableField
    private String itemThreeMaxEvaluation;
    @ApiModelProperty(value="删除标记")
    @TableField
    private Integer deleteFlag;
}
