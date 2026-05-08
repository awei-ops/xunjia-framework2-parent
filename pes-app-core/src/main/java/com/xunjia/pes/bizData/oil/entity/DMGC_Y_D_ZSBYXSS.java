package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("站设备运行时数动态日数据")
@TableName("dmgc_y_d_zsbyxss")
@Data
public class DMGC_Y_D_ZSBYXSS extends BaseEntity {

    @ApiModelProperty("设备ID")
    @TableField
    private String sbid;

    @ApiModelProperty("设备名称")
    @TableField
    @ExportColumn(name = "设备名称", sort = 3)
    private String sbmc;

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ExportColumn(name = "日期", sort = 2)
    private Date rq;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("所属站库表名")
    @TableField("SSZK_TABLENAME")
    private String sszkTableName;

    @ApiModelProperty("所属站库名")
    @TableField("SSZK_NAME")
    @ExportColumn(name = "所属站名", sort = 1)
    private String sszkName;

    @ApiModelProperty("设备类别")
    @TableField
    @ExportColumn(name = "设备类别", sort = 6)
    private String sblb;

    @ApiModelProperty("设备编号")
    @TableField
    @ExportColumn(name = "设备编号", sort = 4)
    private String sbbh;

    @ApiModelProperty("运行时数")
    @TableField
    @ExportColumn(name = "运行时数", sort = 5)
    private int yxss;

    @ApiModelProperty("设备运行状况")
    @TableField
    @ExportColumn(name = "设备运行状况", sort = 7)
    private String sbyxzk;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 9)
    private String bz;

    @ApiModelProperty("所属设备表名")
    @TableField("SSSB_TABLENAME")
    private String sssbTableName;

    @ApiModelProperty("泵效")
    @TableField(exist = false)
    @ExportColumn(name = "泵效", sort = 8)
    private String bx;

    @ApiModelProperty("所属站库主键")
    @TableField("SSZK_EVENTID")
    private String sszkEventId;
}
