package com.xunjia.pes.bizData.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("集输系统耗电")
@TableName("report_oil_consume_power")
@Data
public class OilConsumePower {

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

    @ApiModelProperty("集输系统-月耗电量合计")
    @TableField(exist = false)
    private Double powerMonthSum;

    @ApiModelProperty("集输系统-月耗电量累计")
    @TableField(exist = false)
    private Double powerSum;

    @ApiModelProperty("月产液量")
    @TableField
    private Double liquidProduction;

    @ApiModelProperty("月产液量累计")
    @TableField(exist = false)
    private Double liquidProductionSum;

    @ApiModelProperty("集输系统-吨液耗电-月均单耗")
    @TableField
    private Double oilPowerUnit;

    @ApiModelProperty("集输系统-吨液耗电-月均单耗累计平均")
    @TableField(exist = false)
    private Double oilPowerUnitSumAve;

    @ApiModelProperty("集输系统-掺水泵台数")
    @TableField(exist = false)
    private Integer pumpCsbCount;

    @ApiModelProperty("集输系统-掺水泵当月运行台数")
    @TableField
    private Integer pumpCsbRunCount;

    @ApiModelProperty("集输系统-掺水泵累计平均运行台数")
    @TableField(exist = false)
    private Double pumpCsbRunCountSumAve;

    @ApiModelProperty("集输系统-掺水泵月平均泵效")
    @TableField
    private Double pumpCsbEfficiencyAve;

    @ApiModelProperty("集输系统-掺水泵月累计平均泵效")
    @TableField(exist = false)
    private Double pumpCsbEfficiencySumAve;

    @ApiModelProperty("集输系统-外输泵台数")
    @TableField(exist = false)
    private Integer pumpWsbCount;

    @ApiModelProperty("集输系统-外输泵当月运行台数")
    @TableField
    private Integer pumpWsbRunCount;

    @ApiModelProperty("集输系统-外输泵累计平均运行台数")
    @TableField(exist = false)
    private Double pumpWsbRunCountSumAve;

    @ApiModelProperty("集输系统-外输泵月平均泵效")
    @TableField
    private Double pumpWsbEfficiencyAve;

    @ApiModelProperty("集输系统-外输泵月累计平均泵效")
    @TableField(exist = false)
    private Double pumpWsbEfficiencySumAve;

    @ApiModelProperty("集输系统-掺水炉台数")
    @TableField(exist = false)
    private Integer furnaceCslCount;

    @ApiModelProperty("集输系统-掺水炉当月运行台数")
    @TableField
    private Integer furnaceCslRunCount;

    @ApiModelProperty("集输系统-掺水炉累计平均运行台数")
    @TableField(exist = false)
    private Double furnaceCslRunCountSumAve;

    @ApiModelProperty("集输系统-掺水炉月平均炉效")
    @TableField
    private Double furnaceCslEfficiencyAve;

    @ApiModelProperty("集输系统-掺水炉月累计平均炉效")
    @TableField(exist = false)
    private Double furnaceCslEfficiencySumAve;

    @ApiModelProperty("集输系统-外输炉台数")
    @TableField(exist = false)
    private Integer furnaceWslCount;

    @ApiModelProperty("集输系统-外输炉当月运行台数")
    @TableField
    private Integer furnaceWslRunCount;

    @ApiModelProperty("集输系统-外输炉累计平均运行台数")
    @TableField(exist = false)
    private Double furnaceWslRunCountSumAve;

    @ApiModelProperty("集输系统-外输炉月平均炉效")
    @TableField
    private Double furnaceWslEfficiencyAve;

    @ApiModelProperty("集输系统-外输炉月累计平均炉效")
    @TableField(exist = false)
    private Double furnaceWslEfficiencySumAve;

    @ApiModelProperty("集输系统-采暖炉台数")
    @TableField(exist = false)
    private Integer furnaceCnlCount;

    @ApiModelProperty("集输系统-采暖炉当月运行台数")
    @TableField
    private Integer furnaceCnlRunCount;

    @ApiModelProperty("集输系统-采暖炉累计平均运行台数")
    @TableField(exist = false)
    private Double furnaceCnlRunCountSumAve;

    @ApiModelProperty("集输系统-采暖炉月平均炉效")
    @TableField
    private Double furnaceCnlEfficiencyAve;

    @ApiModelProperty("集输系统-采暖炉月累计平均炉效")
    @TableField(exist = false)
    private Double furnaceCnlEfficiencySumAve;
}
