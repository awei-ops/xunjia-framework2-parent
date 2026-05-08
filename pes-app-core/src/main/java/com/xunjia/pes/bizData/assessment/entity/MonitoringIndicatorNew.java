package com.xunjia.pes.bizData.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监测项目与指标要求New")
@TableName("t_monitoring_new")
@Data
public class MonitoringIndicatorNew {
    @ApiModelProperty(value = "id")
    @TableId
    private String id;
    @ApiModelProperty("监测项目与指标要求名称")
    @TableField
    private String typeName;
    @ApiModelProperty("分组值上限(X</X<=)")
    @TableField
    private Double valueMax;
    @ApiModelProperty("分组值下限(X>/X>=)")
    @TableField
    private Double valueMin;
    @ApiModelProperty("监测项目")
    @TableField
    private String monitoringItem;

    @ApiModelProperty("计量单位")
    @TableField
    private String unitOfMeasurement;
    @ApiModelProperty("监测项目限定值")
    @TableField
    private Double monitoringItemLimit;
    @ApiModelProperty("监测项目限定值对应分值")
    @TableField
    private Double monitoringItemLimitScore;
    @ApiModelProperty("监测项目节能(标杆)值")
    @TableField
    private Double monitoringItemEnergy;
    @ApiModelProperty("监测项目节能值(标杆)对应分值")
    @TableField
    private Double monitoringItemEnergyScore;
    @ApiModelProperty("监测项目最低评价")
    @TableField
    private String itemMinEvaluation;
    @ApiModelProperty("监测项目中等评价")
    @TableField
    private String itemMidEvaluation;
    @ApiModelProperty("监测项目最高评价")
    @TableField
    private String itemMaxEvaluation;
    @ApiModelProperty("监测项目最小值")
    @TableField
    private double monitoringItemMin;
    @ApiModelProperty("监测项目最小值对应分值")
    @TableField
    private double monitoringItemMinScore;
    @ApiModelProperty("监测项目最大值")
    @TableField
    private double monitoringItemMax;
    @ApiModelProperty("监测项目最大值对应分值")
    @TableField
    private double monitoringItemMaxScore;
    @ApiModelProperty("排序")
    @TableField
    private Integer orderNo;
}
