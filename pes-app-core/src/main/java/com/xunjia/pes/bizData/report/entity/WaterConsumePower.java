package com.xunjia.pes.bizData.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("注水系统耗电")
@TableName("report_water_consume_power")
@Data
public class WaterConsumePower {
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("作业区名称")
    @TableField
    private String areaName;

    @ApiModelProperty("站名称")
    @TableField
    private String stationName;

    @ApiModelProperty("年")
    @TableField
    private Integer year;

    @ApiModelProperty("月")
    @TableField
    private Integer month;

    @ApiModelProperty("注水系统-月耗电量")
    @TableField
    private Double waterInjectPower;

    @ApiModelProperty("注水系统-月耗电量累计")
    @TableField(exist = false)
    private Double waterInjectPowerSum;

    @ApiModelProperty("月泵水量")
    @TableField
    private Double pumpingWater;

    @ApiModelProperty("月泵水量累计")
    @TableField(exist = false)
    private Double pumpingWaterSum;

    @ApiModelProperty("注水系统-泵水单耗-月均单耗")
    @TableField
    private Double waterInjectPumpingUnit;

    @ApiModelProperty("注水系统-泵水单耗-月均单耗累计平均")
    @TableField(exist = false)
    private Double waterInjectPumpingUnitSumAve;

    @ApiModelProperty("注水系统-泵台数")
    @TableField(exist = false)
    private Integer pumpCount;

    @ApiModelProperty("注水系统-当月运行台数")
    @TableField
    private Integer pumpRunCount;

    @ApiModelProperty("注水系统-累计平均运行台数")
    @TableField(exist = false)
    private Double pumpRunCountSumAve;

    @ApiModelProperty("注水系统-月平均泵效")
    @TableField
    private Double pumpEfficiencyAve;

    @ApiModelProperty("注水系统-月累计平均泵效")
    @TableField(exist = false)
    private Double pumpEfficiencySumAve;
}
