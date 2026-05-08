package com.xunjia.pes.bizData.waterInjection.entity;

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

@ApiModel("注水泵生产运行日数据")
@TableName("dmgc_s_d_zsbrsj")
@Data
public class DMGC_S_D_ZSBRSJ extends BaseEntity {

    @ApiModelProperty("注水站名称")
    @TableField
    @ExportColumn(name = "注水站名称", sort = 1)
    @ExamineColumn(name = "注水站名称", sort = 1)
    private String zszName;

    @ApiModelProperty("泵名称")
    @TableField
    @ExportColumn(name = "泵名称", sort = 2)
    @ExamineColumn(name = "泵名称", sort = 2)
    private String jbName;

    @ApiModelProperty("站内编号")
    @TableField
    @ExportColumn(name = "机泵编号", sort = 3)
    @ExamineColumn(name = "站内编号", sort = 3)
    private String bbh;

    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @TableField
    @ExportColumn(name = "日期", sort = 4)
    @ExamineColumn(name = "日期", sort = 4)
    private Date rq;

    @ApiModelProperty("泵效")
    @TableField
    @ExportColumn(name = "泵效", sort = 15)
    @ExamineColumn(name = "泵效(%)", sort = 5)
    private Double bx;

    @ApiModelProperty("泵效得分")
    @TableField
    @ExamineColumn(name = "泵效得分", sort = 6)
    private Double bxScore;

    @ApiModelProperty("泵效权重")
    @TableField
    @ExamineColumn(name = "泵效权重", sort = 7)
    private Double weightBx;

    @ApiModelProperty("泵效权重得分")
    @TableField
    @ExamineColumn(name = "泵效权重得分", sort = 8)
    private Double weightBxScore;

    @ApiModelProperty("泵效评价")
    @TableField
    @ExamineColumn(name = "泵效评价", sort = 9)
    private String bxpj;

    @ApiModelProperty("节流损失率")
    @TableField
    @ExamineColumn(name = "节流损失率(%)", sort = 10)
    private Double jlssl;

    @ApiModelProperty("节流损失率得分")
    @TableField
    @ExamineColumn(name = "节流损失率得分", sort = 11)
    private Double jlsslScore;

    @ApiModelProperty("节流损失率权重")
    @TableField
    @ExamineColumn(name = "节流损失率权重", sort = 12)
    private Double weightJlssl;

    @ApiModelProperty("节流损失率权重得分")
    @TableField
    @ExamineColumn(name = "节流损失率权重得分", sort = 13)
    private Double weightJlsslScore;

    @ApiModelProperty("节流损失率评价")
    @TableField
    @ExamineColumn(name = "节流损失率评价", sort = 14)
    private String jlsslpj;

    @ApiModelProperty("回流率(%)")
    @TableField
    @ExamineColumn(name = "回流率", sort = 15)
    private Double hlRate;

    @ApiModelProperty("回流率得分")
    @TableField
    @ExamineColumn(name = "回流率得分", sort = 16)
    private Double hlRateScore;

    @ApiModelProperty("回流率权重")
    @TableField
    @ExamineColumn(name = "回流率权重", sort = 17)
    private Double weightHlRate;

    @ApiModelProperty("回流率权重得分")
    @TableField
    @ExamineColumn(name = "回流率权重得分", sort = 18)
    private Double weightHlRateScore;

    @ApiModelProperty("回流率评价")
    @TableField
    @ExamineColumn(name = "回流率评价", sort = 19)
    private String hlRatepj;

    @ApiModelProperty("负荷率")
    @TableField
    @ExamineColumn(name = "负荷率(%)", sort = 20)
    private Double fhl;

    @ApiModelProperty("负荷率得分")
    @TableField
    @ExamineColumn(name = "负荷率得分", sort = 21)
    private Double fhlScore;

    @ApiModelProperty("负荷率权重")
    @TableField
    @ExamineColumn(name = "负荷率权重", sort = 22)
    private Double weightFhl;

    @ApiModelProperty("负荷率权重得分")
    @TableField
    @ExamineColumn(name = "负荷率权重得分", sort = 23)
    private Double weightFhlScore;

    @ApiModelProperty("负荷率评价")
    @TableField
    @ExamineColumn(name = "负荷率评价", sort = 24)
    private String fhlpj;

    @ApiModelProperty("机泵得分")
    @TableField
    @ExamineColumn(name = "机泵得分", sort = 25)
    private Double jbScore;

    @ApiModelProperty("机泵评价")
    @TableField
    @ExamineColumn(name = "机泵评价", sort = 26)
    private String jbpj;

    @ApiModelProperty("机泵ID")
    @TableField("JB_EVENTID")
    private String jbEventId;

    @ApiModelProperty("泵规格")
    @TableField
    private String bgg;

    @ApiModelProperty("来水水质类型")
    @TableField
    @ExportColumn(name = "来水水质类型", sort = 5)
    private String lsszlx;

    @ApiModelProperty("泵出口压力")
    @TableField
    @ExportColumn(name = "泵出口压力", sort = 6)
    private Double bckyl;

    @ApiModelProperty("泵入口压力")
    @TableField
    @ExportColumn(name = "泵入口压力", sort = 7)
    private Double brkyl;

    @ApiModelProperty("泵水量")
    @TableField
    @ExportColumn(name = "泵水量", sort = 8)
    private Double bsl;

    @ApiModelProperty("运行时数")
    @TableField
    @ExportColumn(name = "运行时数", sort = 9)
    private Double yxss;

    @ApiModelProperty("运行状态")
    @TableField
    private String yxzt;

    @ApiModelProperty("进口水温")
    @TableField
    @ExportColumn(name = "进口水温", sort = 10)
    private Double jksw;

    @ApiModelProperty("出口水温")
    @TableField
    @ExportColumn(name = "出口水温", sort = 11)
    private Double cksw;

    @ApiModelProperty("功率因数")
    @TableField
    private Double glys;

    @ApiModelProperty("泵所属汇管名称")
    @TableField
    private String bsshgmc;

    @ApiModelProperty("泵所属汇管压力")
    @TableField
    private Double bsshgyl;

    @ApiModelProperty("泵所属干线名称")
    @TableField
    private String bssgxmc;

    @ApiModelProperty("泵所属干线压力")
    @TableField
    private Double bssgxyl;

    @ApiModelProperty("电机前轴瓦温度")
    @TableField
    private Double djqzwwd;

    @ApiModelProperty("电机后轴瓦温度")
    @TableField
    private Double djhzwwd;

    @ApiModelProperty("电机定子温度")
    @TableField
    private Double djdzwd;

    @ApiModelProperty("温差")
    @TableField
    private Double wc;

    @ApiModelProperty("管线压力")
    @TableField
    @ExportColumn(name = "管线压力", sort = 12)
    private Double gxyl;

    @ApiModelProperty("泵管压差")
    @TableField
    @ExportColumn(name = "泵管压差", sort = 13)
    private Double bgyc;

    @ApiModelProperty("流量")
    @TableField
    private Double ll;

    @ApiModelProperty("电流")
    @TableField
    private Double dl;

    @ApiModelProperty("电压")
    @TableField
    private Double dy;

    @ApiModelProperty("用电量")
    @TableField
    @ExportColumn(name = "用电量", sort = 14)
    private Double rydl;

    @ApiModelProperty("泵水单耗")
    @TableField
    @ExportColumn(name = "泵水单耗", sort = 16)
    private Double bsdh;

    @ApiModelProperty("影响注水量")
    @TableField
    private Double ryxzrl;

    @ApiModelProperty("影响因素")
    @TableField
    private String yxys;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("等熵温差修正值")
    @TableField
    private Double dswcxzz;

    @ApiModelProperty("所属站库id")
    @TableField("SSZK_EVENTID")
    private String sszkEventId;

    @ApiModelProperty("机泵类型")
    @TableField
    private String jbType;

    @ApiModelProperty("回流量")
    @TableField
    private Double hll;

    @ApiModelProperty("录入的数据是否已经审核")
    @TableField
    private Boolean dataAlreadyAudited;

    @ApiModelProperty("机泵权重机泵按权重（额定功率）比")
    @TableField
    private Double weightJb;

    @ApiModelProperty("机泵权重得分")
    @TableField
    private Double weightJbScore;
}
