package com.xunjia.pes.bizData.waterTreatment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("水处理站设备运行日数据")
@TableName("dmgc_s_d_sbyx")
@Data
public class DMGC_S_D_SBYX extends BaseEntity {

    @ApiModelProperty("设备id")
    @TableField
    private String sbid;

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ExportColumn(name = "日期", sort = 2)
    private Date rq;

    @ApiModelProperty("设备类型")
    @TableField
    @ExportColumn(name = "设备类型", sort = 5)
    private String sblx;

    @ApiModelProperty("站内编号")
    @TableField
    @ExportColumn(name = "所属站ID", sort = 4)
    private String sbbh;

    @ApiModelProperty("运行状态")
    @TableField
    @ExportColumn(name = "运行状态", sort = 6)
    private String yxzt;

    @ApiModelProperty("运行时间")
    @TableField
    @ExportColumn(name = "运行时数", sort = 7)
    private double yxsj;

    @ApiModelProperty("所属设备表名")
    @TableField
    private String sssbbm;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 8)
    private String bz;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("设备名称")
    @TableField(exist = false)
    @ExportColumn(name = "设备名称", sort = 3)
    private String mc;

    @ApiModelProperty("所属站ID")
    @TableField("ZK_EVENTID")
    private String zkEventID;

    @ApiModelProperty("所属站名")
    @TableField("SSZM")
    @ExportColumn(name = "设备ID", sort = 1)
    private String sszm;

    @ApiModelProperty("泵效")
    @TableField(exist = false)
    @ExportColumn(name = "泵效", sort = 8)
    private String bx;

//    @ApiModelProperty("泵运行状态区分")
//    @TableField
//    private String runningState;
//
//    @ApiModelProperty("对应标准")
//    @TableField
//    private String dybz;
//
//    @ApiModelProperty("对应分值")
//    @TableField
//    private double dyfz;
//
//    @ApiModelProperty("泵效评价")
//    @TableField
//    private String bxpj;
}
