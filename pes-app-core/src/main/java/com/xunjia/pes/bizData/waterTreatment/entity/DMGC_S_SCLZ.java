package com.xunjia.pes.bizData.waterTreatment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("水处理站基础信息")
@TableName("dmgc_s_sclz")
@Data
public class DMGC_S_SCLZ extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("投产日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date tcrq;

    @ApiModelProperty("站类型")
    @TableField
    private String zlx;

    @ApiModelProperty("设计规模")
    @TableField
    private double sjgm;

    @ApiModelProperty("工艺流程")
    @TableField
    private String gylc;

    @ApiModelProperty("来水税制类型")
    @TableField
    private String lsszlx;

    @ApiModelProperty("设计出水含油量")
    @TableField
    private int sjcshyl;

    @ApiModelProperty("设计出水悬浮固体含量")
    @TableField
    private int sjcsxfwgthl;

    @ApiModelProperty("设计出水粒径中值")
    @TableField
    private String sjcsljzz;

    @ApiModelProperty("外输口数量")
    @TableField
    private int wsksl;

    @ApiModelProperty("供水方向")
    @TableField
    private String gsfx;

    @ApiModelProperty("采出原水来水口数量")
    @TableField
    private int csyslsksl;

    @ApiModelProperty("采出原水来水站名称")
    @TableField
    private String csyslszmc;

    @ApiModelProperty("普通站来水口数量")
    @TableField
    private int ptzlsksl;

    @ApiModelProperty("普通水来水战名称")
    @TableField
    private String ptslszmc;

    @ApiModelProperty("是否分站采集来水量")
    @TableField
    private int sffzcjlsl;

    @ApiModelProperty("是否分段采集出口水质")
    @TableField
    private String sffdcjcksz;

    @ApiModelProperty("采暖方式")
    @TableField
    private String cnfs;

    @ApiModelProperty("消防方式")
    @TableField
    private String xffs;

    @ApiModelProperty("总建筑面积")
    @TableField
    private double jzmj;

    @ApiModelProperty("主体建筑结构")
    @TableField
    private String jzjg;

    @ApiModelProperty("占地面积")
    @TableField
    private double zdmj;

    @ApiModelProperty("装机负荷")
    @TableField
    private double zjfh;

    @ApiModelProperty("地理位置")
    @TableField
    private String dlwz;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("供水方向id")
    @TableField("GSFX_EVENTID")
    private String gsfxEventId;

    @ApiModelProperty("所属联合站名称")
    @TableField
    private String sslhzmc;

    @ApiModelProperty("编码")
    @TableField
    private String code;

    @ApiModelProperty("所属联合站id")
    @TableField
    private String sslhzid;

    @ApiModelProperty("报废日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date bfrq;

    @ApiModelProperty("运行状态")
    @TableField
    private String yxzt;

    @ApiModelProperty("站型")
    @TableField
    private String zx;

    @ApiModelProperty("集成状态")
    @TableField
    private String jczt;

    @ApiModelProperty("是否数字化")
    @TableField
    private String sfszh;

    @ApiModelProperty("是否无人值守")
    @TableField
    private String sfwrzs;

    @ApiModelProperty("用工人数")
    @TableField
    private int ygrs;

    @ApiModelProperty("周边环境")
    @TableField
    private String zbhj;

    @ApiModelProperty("作业区名称")
    @TableField
    private String zyqName;
}
