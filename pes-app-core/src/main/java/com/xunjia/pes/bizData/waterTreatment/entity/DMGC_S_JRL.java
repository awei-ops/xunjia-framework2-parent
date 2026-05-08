package com.xunjia.pes.bizData.waterTreatment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("水处理加热炉信息")
@TableName("dmgc_s_jrl")
@Data
public class DMGC_S_JRL extends BaseEntity {

    @ApiModelProperty("所属站类型")
    @TableField
    private String zlx;

    @ApiModelProperty("所属站名")
    @TableField
    private String sszm;

    @ApiModelProperty("加热炉名称")
    @TableField
    private String mc;

    @ApiModelProperty("站内编号")
    @TableField
    private String bh;

    @ApiModelProperty("规格型号")
    @TableField
    private String ggxh;

    @ApiModelProperty("加热炉类型")
    @TableField
    private String jrllx;

    @ApiModelProperty("换热盘管数")
    @TableField
    private int hrpgs;

    @ApiModelProperty("得能工作压力")
    @TableField
    private double edyl;

    @ApiModelProperty("额定处理量")
    @TableField
    private double edcll;

    @ApiModelProperty("被加热介质类型")
    @TableField
    private String jrjz;

    @ApiModelProperty("安全保护类型")
    @TableField
    private String aqbhlx;

    @ApiModelProperty("加热炉用途")
    @TableField
    private String jrlyt;

    @ApiModelProperty("额定热负荷")
    @TableField
    private double edrfh;

    @ApiModelProperty("燃料类型")
    @TableField
    private String rllx;

    @ApiModelProperty("设计温度")
    @TableField
    private double sjwd;

    @ApiModelProperty("生产日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date scrq;

    @ApiModelProperty("投用日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date tyrq;

    @ApiModelProperty("生产厂家")
    @TableField
    private String sccj;

    @ApiModelProperty("燃烧器型号")
    @TableField
    private String rsqxh;

    @ApiModelProperty("燃烧器厂家")
    @TableField
    private String rsqcj;

    @ApiModelProperty("燃烧器功率")
    @TableField
    private double rsqgl;

    @ApiModelProperty("燃烧器生产日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date rsqscrq;

    @ApiModelProperty("资产号")
    @TableField
    private String zch;

    @ApiModelProperty("是否报废")
    @TableField
    private String sfbf;

    @ApiModelProperty("报废日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date bfrq;

    @ApiModelProperty("更换日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date ghrq;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

//    @ApiModelProperty("附件")
//    @TableField
//    private String qtfj;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("所属站库id")
    @TableField
    private String sszkid;

    @ApiModelProperty("所属站库表名称")
    @TableField
    private String sszkbmc;

    @ApiModelProperty("更换前eventId")
    @TableField("CEVENTID")
    private String cEventId;
}
