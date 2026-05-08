package com.xunjia.pes.bizData.waterInjection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("注水站基础信息")
@TableName("dmgc_s_zsz")
@Data
public class DMGC_S_ZSZ extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("站类型")
    @TableField
    private String zlx;

    @ApiModelProperty("集成状态")
    @TableField
    private String jczt;

    @ApiModelProperty("工艺流程")
    @TableField
    private String gylc;

    @ApiModelProperty("注水水质种类")
    @TableField
    private int zrszzl;

    @ApiModelProperty("设计规模")
    @TableField
    private double sjnl;

    @ApiModelProperty("普通水设计规模")
    @TableField
    private double sjybsnl;

    @ApiModelProperty("深度水设计规模")
    @TableField
    private double sjsdsnl;

    @ApiModelProperty("清水设计规模")
    @TableField
    private double sjqsnl;

    @ApiModelProperty("设计注水压力")
    @TableField
    private double sjzsyl;

    @ApiModelProperty("水来源")
    @TableField
    private String sly;

    @ApiModelProperty("水去向")
    @TableField
    private String sqx;

    @ApiModelProperty("采暖方式")
    @TableField
    private String cnfs;

    @ApiModelProperty("消防方式")
    @TableField
    private String xffs;

    @ApiModelProperty("主体建筑结构")
    @TableField
    private String jzjg;

    @ApiModelProperty("总建筑面积")
    @TableField
    private int jzmj;

    @ApiModelProperty("占地面积")
    @TableField
    private int zdmj;

    @ApiModelProperty("装机负荷")
    @TableField
    private double zjfh;

    @ApiModelProperty("冷却系统")
    @TableField
    private String sssclzmc;

    @ApiModelProperty("所属联合站名称")
    @TableField
    private String lhzmc;

    @ApiModelProperty("相关站名")
    @TableField
    private String xgzm;

    @ApiModelProperty("投产日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date tcrq;

    @ApiModelProperty("是否数字化")
    @TableField
    private String sfszh;

    @ApiModelProperty("是否无人值守")
    @TableField
    private String sfwrzs;

    @ApiModelProperty("用工人数")
    @TableField
    private int ygrs;

    @ApiModelProperty("运行状态")
    @TableField
    private String yxzt;

    @ApiModelProperty("报废日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date bfrq;

    @ApiModelProperty("周边环境")
    @TableField
    private String zbhj;

    @ApiModelProperty("地理位置")
    @TableField
    private String dlwz;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("工艺流程图")
    @TableField
    private String gylct;

    @ApiModelProperty("平面布置图")
    @TableField
    private String pmbzt;

    @ApiModelProperty("附件")
    @TableField
    private String qtfj;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("水来源id")
    @TableField("SLY_EVENTID")
    private String slyEventId;

    @ApiModelProperty("水去向id")
    @TableField("SQX_EVENTID")
    private String sqxEventId;

    @ApiModelProperty("注水站代码")
    @TableField
    private String code;

    @ApiModelProperty("数据湖中数据id")
    @TableField("U_ID")
    private String uId;

    @ApiModelProperty("数据来源")
    @TableField("DATA_FROM")
    private String dataFrom;

    @ApiModelProperty("作业区名称")
    @TableField
    private String zyqName;

}
