package com.xunjia.pes.bizData.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("能耗月报")
@TableName("report_monthly_energy")
@Data
public class MonthlyEnergy {

    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("作业区名称")
    @TableField
    private String areaName;

    @ApiModelProperty("年")
    @TableField
    private Integer year;

    @ApiModelProperty("月")
    @TableField
    private Integer month;

    @ApiModelProperty("集输系统-站场-月耗电量")
    @TableField
    private Double oilStationPower;

    @ApiModelProperty("集输系统-站场-月耗电量累计")
    @TableField(exist = false)
    private Double oilStationPowerSum;

    @ApiModelProperty("集输系统-电加热-月耗电量")
    @TableField
    private Double oilHeatingPower;

    @ApiModelProperty("集输系统-电加热-月耗电量累计")
    @TableField(exist = false)
    private Double oilHeatingPowerSum;

    @ApiModelProperty("集输系统-气系统-月耗电量")
    @TableField
    private Double oilGasPower;

    @ApiModelProperty("集输系统-气系统-月耗电量累计")
    @TableField(exist = false)
    private Double oilGasPowerSum;

    @ApiModelProperty("注水系统-月耗电量")
    @TableField
    private Double waterInjectPower;

    @ApiModelProperty("注水系统-月耗电量累计")
    @TableField(exist = false)
    private Double waterInjectPowerSum;

    @ApiModelProperty("水处理系统-月耗电量")
    @TableField
    private Double waterTreatmentPower;

    @ApiModelProperty("水处理系统-月耗电量累计")
    @TableField(exist = false)
    private Double waterTreatmentPowerSum;

    @ApiModelProperty("供水系统-月耗电量")
    @TableField
    private Double waterSupplyPower;

    @ApiModelProperty("供水系统-月耗电量累计")
    @TableField(exist = false)
    private Double waterSupplyPowerSum;

    @ApiModelProperty("月耗气量")
    @TableField
    private Double consumeGas;

    @ApiModelProperty("月耗气量累计")
    @TableField(exist = false)
    private Double consumeGasSum;

    @ApiModelProperty("月耗能耗量")
    @TableField(exist = false)
    private Double energyConsumption;

    @ApiModelProperty("月耗能耗量累计")
    @TableField(exist = false)
    private Double energyConsumptionSum;

    @ApiModelProperty("月产液量")
    @TableField
    private Double liquidProduction;

    @ApiModelProperty("月产液量累计")
    @TableField(exist = false)
    private Double liquidProductionSum;

    @ApiModelProperty("月产油量")
    @TableField
    private Double oilProduction;

    @ApiModelProperty("月产油量累计")
    @TableField(exist = false)
    private Double oilProductionSum;

    @ApiModelProperty("月泵水量")
    @TableField
    private Double pumpingWater;

    @ApiModelProperty("月泵水量累计")
    @TableField(exist = false)
    private Double pumpingWaterSum;

    @ApiModelProperty("月水处理量")
    @TableField
    private Double waterTreatment;

    @ApiModelProperty("月水处理量累计")
    @TableField(exist = false)
    private Double waterTreatmentSum;

    @ApiModelProperty("集输系统-吨液耗电-月均单耗")
    @TableField
    private Double oilPowerUnit;

    @ApiModelProperty("集输系统-吨液耗电-月均单耗累计平均")
    @TableField(exist = false)
    private Double oilPowerUnitSumAve;

    @ApiModelProperty("集输系统-吨液耗气-月均单耗")
    @TableField
    private Double oilGasUnit;

    @ApiModelProperty("集输系统-吨液耗气-月均单耗累计平均")
    @TableField(exist = false)
    private Double oilGasUnitSumAve;

    @ApiModelProperty("集输系统-吨油耗电-月均单耗")
    @TableField(exist = false)
    private Double petroleumPowerUnit;

    @ApiModelProperty("集输系统-吨油耗电-月均单耗累计平均")
    @TableField(exist = false)
    private Double petroleumPowerUnitSumAve;

    @ApiModelProperty("集输系统-吨油耗气-月均单耗")
    @TableField(exist = false)
    private Double petroleumGasUnit;

    @ApiModelProperty("集输系统-吨油耗气-月均单耗累计平均")
    @TableField(exist = false)
    private Double petroleumGasUnitSumAve;

    @ApiModelProperty("注水系统-泵水单耗-月均单耗")
    @TableField
    private Double waterInjectPumpingUnit;

    @ApiModelProperty("注水系统-泵水单耗-月均单耗累计平均")
    @TableField(exist = false)
    private Double waterInjectPumpingUnitSumAve;
}
