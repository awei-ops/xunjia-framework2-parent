package com.xunjia.pes.bizData.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("采油十厂首页统计")
@TableName("report_home_page")
@Data
public class HomePage {
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("年")
    @TableField
    private Integer year;

    @ApiModelProperty("油田耗气")
    @TableField(exist = false)
    private Double oilfieldGasConsumption;

    @ApiModelProperty("气井产气")
    @TableField
    private Double gasWellProduction;

    @ApiModelProperty("深层来气")
    @TableField
    private Double deepGas;

    @ApiModelProperty("哈市供气")
    @TableField
    private Double harbinGasSupply;

    @ApiModelProperty("吨液耗气")
    @TableField(exist = false)
    private Double oilGasUnitSumAve;

    @ApiModelProperty("油田耗电")
    @TableField(exist = false)
    private Double oilfieldPowerConsumption;

    @ApiModelProperty("集输系统-耗电量累计")
    @TableField(exist = false)
    private Double oilPowerSum;

    @ApiModelProperty("注水系统-耗电量累计")
    @TableField(exist = false)
    private Double waterInjectPowerSum;

    @ApiModelProperty("水处理系统-耗电量")
    @TableField(exist = false)
    private Double waterTreatmentPowerSum;

    @ApiModelProperty("供水系统-耗电量累计")
    @TableField(exist = false)
    private Double waterSupplyPowerSum;

    @ApiModelProperty("注水系统-泵水单耗")
    @TableField(exist = false)
    private Double waterInjectPumpingUnitSumAve;

    @ApiModelProperty("集输系统-吨液耗电")
    @TableField(exist = false)
    private Double oilPowerUnitSumAve;
}
