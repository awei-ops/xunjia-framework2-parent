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

@ApiModel("水处理站生产动态日数据")
@TableName("dmgc_s_d_sclzrsj")
@Data
public class DMGC_S_D_SCLZRSJ extends BaseEntity {

    @ApiModelProperty("水处理站id")
    @TableField("ZK_EVENTID")
    private String zkEventId;

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ExportColumn(name = "日期", sort = 2)
    private Date rq;

    @ApiModelProperty("来水水量")
    @TableField
    @ExportColumn(name = "来水水量", sort = 3)
    private Double lssl;

    @ApiModelProperty("外输水量")
    @TableField
    @ExportColumn(name = "外输水量", sort = 4)
    private Double rwssl;

    @ApiModelProperty("系统负荷率")
    @TableField
    @ExportColumn(name = "系统负荷率", sort = 5)
    private Double xtfhl;

    @ApiModelProperty("处理水质类型")
    @TableField
    @ExportColumn(name = "处理水质类型", sort = 6)
    private String clszlx;

    @ApiModelProperty("来水含水量")
    @TableField
    @ExportColumn(name = "来水含水量", sort = 7)
    private Double lshyl;

    @ApiModelProperty("来水悬浮固体含量")
    @TableField
    @ExportColumn(name = "来水悬浮固体含量", sort = 8)
    private Double lsxfgthl;

    @ApiModelProperty("外输含油量")
    @TableField
    @ExportColumn(name = "外输含油量", sort = 9)
    private Double wshyl;

    @ApiModelProperty("外输悬浮固体含量")
    @TableField
    @ExportColumn(name = "外输悬浮固体含量", sort = 10)
    private Double rwsql;

    @ApiModelProperty("外输粒径中值")
    @TableField
    @ExportColumn(name = "外输粒径中值", sort = 11)
    private Double wsljzz;

    @ApiModelProperty("反冲洗水量")
    @TableField
    @ExportColumn(name = "反冲洗水量", sort = 12)
    private Double fcxsl;

    @ApiModelProperty("收油量")
    @TableField
    @ExportColumn(name = "收油量", sort = 13)
    private Double rsyl;

    @ApiModelProperty("耗电量")
    @TableField
    @ExportColumn(name = "耗电量", sort = 14)
    private Double rhdl;

    @ApiModelProperty("单耗")
    @TableField
    @ExportColumn(name = "单耗", sort = 15)
    private Double dh;

    @ApiModelProperty("加药总量")
    @TableField
    @ExportColumn(name = "加药总量", sort = 16)
    private Double rjyzl;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 17)
    private String bz;

    @ApiModelProperty("处理水量")
    @TableField
    @ExportColumn(name = "处理水量", sort = 18)
    private Double rclsl;

    @ApiModelProperty("是否允许删除")
    @TableField("ALLOW_DELETE")
    private int allowDelete;

    @ApiModelProperty("原水来水量")
    @TableField
    @ExportColumn(name = "原水来水量", sort = 19)
    private Double yslsl;

    @ApiModelProperty("普通站来水量")
    @TableField
    @ExportColumn(name = "普通站来水量", sort = 20)
    private Double ptzlsl;

    @ApiModelProperty("站名称")
    @TableField
    @ExportColumn(name = "站名称", sort = 1)
    private String zmc;

    @ApiModelProperty("单耗得分")
    @TableField
    private Double dhScore;

    @ApiModelProperty("单耗权重")
    @TableField
    private Double dhWeight;

    @ApiModelProperty("单耗权重得分")
    @TableField
    private Double dhWeightScore;

    @ApiModelProperty("单耗评价")
    @TableField
    private String dhPj;

    @ApiModelProperty("外输污水单耗 = 耗电量/外输水量")
    @TableField
    private Double wswsdh;

    @ApiModelProperty("外输污水单耗打分")
    @TableField
    private Double wswsdhScore;

    @ApiModelProperty("外输污水单耗权重")
    @TableField
    private Double wswsdhWeight;

    @ApiModelProperty("外输污水单耗权重得分")
    @TableField
    private Double wswsdhWeightScore;

    @ApiModelProperty("外输污水单耗评价")
    @TableField
    private String wswsdhPj;

    @ApiModelProperty("本站该日综合得分")
    @TableField
    private Double jxpjScore;
}
