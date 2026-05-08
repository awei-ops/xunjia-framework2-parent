package com.xunjia.pes.bizData.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("耗气月报")
@TableName("report_consume_gas")
@Data
public class ConsumeGasPower {

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

    @ApiModelProperty("月耗气量")
    @TableField
    private Double consumeGas;

    @ApiModelProperty("月耗气量累计")
    @TableField(exist = false)
    private Double consumeGasSum;

    @ApiModelProperty("月产液量")
    @TableField
    private Double liquidProduction;

    @ApiModelProperty("月产液量累计")
    @TableField(exist = false)
    private Double liquidProductionSum;

    @ApiModelProperty("吨液耗气-月均单耗")
    @TableField
    private Double gasUnit;

    @ApiModelProperty("吨液耗气-月均单耗累计平均")
    @TableField(exist = false)
    private Double gasUnitSumAve;

    @ApiModelProperty("月产油量")
    @TableField
    private Double oilProduction;

    @ApiModelProperty("月产油量累计")
    @TableField(exist = false)
    private Double oilProductionSum;

    @ApiModelProperty("吨油耗气-月均单耗")
    @TableField(exist = false)
    private Double petroleumGasUnit;

    @ApiModelProperty("吨油耗气-月均单耗累计平均")
    @TableField(exist = false)
    private Double petroleumGasUnitSumAve;
}
