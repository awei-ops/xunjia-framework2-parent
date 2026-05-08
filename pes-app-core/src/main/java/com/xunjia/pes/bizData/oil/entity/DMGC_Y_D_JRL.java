package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExamineColumn;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("加热炉运行动态日数据")
@TableName("dmgc_y_d_jrl")
@Data
public class DMGC_Y_D_JRL extends BaseEntity {

    @ApiModelProperty("所属站库名称")
    @ExportColumn(name = "所属站库名称", sort = 1)
    @ExamineColumn(name = "所属站库名称", sort = 1)
    @TableField("SSZK_NAME")
    private String sszkName;

    @ApiModelProperty("名称")
    @TableField
    @ExportColumn(name = "名称", sort = 3)
    @ExamineColumn(name = "名称", sort = 2)
    private String mc;

    @ApiModelProperty("站内编号")
    @TableField
    @ExamineColumn(name = "站内编号", sort = 3)
    private String znbh;

    @ApiModelProperty("日期")
    @TableField
    @ExportColumn(name = "日期", sort = 2)
    @ExamineColumn(name = "日期", sort = 4)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date rq;

    @ApiModelProperty("所属站库主键")
    @TableField("SSZK_EVENTID")
    private String sszkEventId;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("加热炉运行状况")
    @TableField
    @ExportColumn(name = "加热炉运行状况", sort = 4)
    private String jrlyxzk;

    @ApiModelProperty("启用时间")
    @TableField
    @ExportColumn(name = "启用时间", sort = 5)
    private Date qysj;

    @ApiModelProperty("停用时间")
    @TableField
    @ExportColumn(name = "停用时间", sort = 6)
    private Date tysj;

    @ApiModelProperty("故障部位")
    @TableField
    @ExportColumn(name = "故障部位", sort = 7)
    private String gzbw;

    @ApiModelProperty("故障原因")
    @TableField
    @ExportColumn(name = "故障原因", sort = 8)
    private String gzyy;

    @ApiModelProperty("运行时数")
    @TableField
    @ExportColumn(name = "运行时数", sort = 9)
    private Integer yxss;

    @ApiModelProperty("加热炉进口温度")
    @TableField
    @ExportColumn(name = "加热炉进口温度", sort = 10)
    private Integer jrljkwd;

    @ApiModelProperty("加热炉出口温度")
    @TableField
    @ExportColumn(name = "加热炉出口温度", sort = 11)
    private Integer jrlckwd;

    @ApiModelProperty("烟气温度")
    @TableField
    @ExportColumn(name = "烟气温度", sort = 12)
    @ExamineColumn(name = "烟气温度(℃)", sort = 10)
    private Integer yqwd;

    @ApiModelProperty("燃料消耗量")
    @TableField
    @ExportColumn(name = "燃料消耗量", sort = 13)
    private Double rlxhl;

    @ApiModelProperty("加热量")
    @TableField
    @ExportColumn(name = "加热量", sort = 14)
    private Integer jrl;

    @ApiModelProperty("烟气含氧量")
    @TableField
    @ExportColumn(name = "烟气含氧量", sort = 15)
    private Double yqhyl;

    @ApiModelProperty("炉效")
    @TableField
    @ExportColumn(name = "炉效", sort = 16)
    @ExamineColumn(name = "炉效(%)", sort = 5)
    private Double lx;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 17)
    private String bz;

    @ApiModelProperty("加热炉ID")
    @TableField("JRLID")
    private String jrlId;

    @ApiModelProperty("加热炉热效率权重")
    @TableField
    @ExamineColumn(name = "热效率权重", sort = 7)
    private Double weightLx;

    @ApiModelProperty("加热炉热效率得分")
    @TableField
    @ExamineColumn(name = "热效率得分", sort = 6)
    private Double lxScore;

    @ApiModelProperty("加热炉热效率权重得分")
    @TableField
    @ExamineColumn(name = "热效率权重得分", sort = 8)
    private Double weightLxScore;

    @ApiModelProperty("加热炉热效率评价")
    @TableField
    @ExamineColumn(name = "热效率评价", sort = 9)
    private String lxpj;

    @ApiModelProperty("空气系数")
    @TableField
    @ExamineColumn(name = "空气系数", sort = 15)
    private Double kqxs;

    @ApiModelProperty("空气系数权重")
    @TableField
    @ExamineColumn(name = "空气系数权重", sort = 17)
    private Double weightKqxs;

    @ApiModelProperty("空气系数得分")
    @TableField
    @ExamineColumn(name = "空气系数得分", sort = 16)
    private Double kqxsScore;

    @ApiModelProperty("空气系数权重得分")
    @TableField
    @ExamineColumn(name = "空气系数权重得分", sort = 18)
    private Double weightKqxsScore;

    @ApiModelProperty("空气系数评价")
    @TableField
    @ExamineColumn(name = "空气系数评价", sort = 19)
    private String kqxspj;

    @ApiModelProperty("排烟温度权重")
    @TableField
    @ExamineColumn(name = "排烟温度权重", sort = 12)
    private Double weightYqwd;

    @ApiModelProperty("排烟温度得分")
    @TableField
    @ExamineColumn(name = "排烟温度得分", sort = 11)
    private Double yqwdScore;

    @ApiModelProperty("排烟温度权重得分")
    @TableField
    @ExamineColumn(name = "排烟温度权重得分", sort = 13)
    private Double weightYqwdScore;

    @ApiModelProperty("排烟温度评价")
    @TableField
    @ExamineColumn(name = "排烟温度评价", sort = 14)
    private String yqwdpj;

    @ApiModelProperty("热负荷")
    @TableField
    @ExamineColumn(name = "热负荷", sort = 20)
    private Double rfh;

    @ApiModelProperty("热负荷权重")
    @TableField
    @ExamineColumn(name = "热负荷权重", sort = 22)
    private Double weightRfh;

    @ApiModelProperty("热负荷得分")
    @TableField
    @ExamineColumn(name = "热负荷得分", sort = 21)
    private Double rfhScore;

    @ApiModelProperty("热负荷权重得分")
    @TableField
    @ExamineColumn(name = "热负荷权重得分", sort = 23)
    private Double weightRfhScore;

    @ApiModelProperty("热负荷评价")
    @TableField
    @ExamineColumn(name = "热负荷评价", sort = 24)
    private String rfhpj;

    //额外增加字段
    @ApiModelProperty("炉体外表面温度")
    @TableField
    @ExamineColumn(name = "炉体外表面温度", sort = 25)
    private Double ltwbmwd;

    @ApiModelProperty("录入的数据是否已经审核")
    @TableField
    private Boolean dataAlreadyAudited;

    @ApiModelProperty("炉体外表面温度权重")
    @TableField
    @ExamineColumn(name = "炉体外表面温度权重", sort = 27)
    private Double weightLtwbmwd;

    @ApiModelProperty("炉体外表面温度得分")
    @TableField
    @ExamineColumn(name = "炉体外表面温度得分", sort = 26)
    private Double ltwbmwdScore;

    @ApiModelProperty("炉体外表面温度权重得分")
    @TableField
    @ExamineColumn(name = "炉体外表面温度权重得分", sort = 28)
    private Double weightLtwbmwdScore;

    @ApiModelProperty("炉体外表面温度评价")
    @TableField
    @ExamineColumn(name = "炉体外表面温度评价", sort = 29)
    private String ltwbmwdpj;

    @ApiModelProperty("加热炉总得分")
    @TableField
    @ExamineColumn(name = "加热炉总得分", sort = 30)
    private Double jxScore;

    @ApiModelProperty("加热炉绩效评价")
    @TableField
    @ExamineColumn(name = "加热炉绩效评价", sort = 31)
    private String jxpj;
}
