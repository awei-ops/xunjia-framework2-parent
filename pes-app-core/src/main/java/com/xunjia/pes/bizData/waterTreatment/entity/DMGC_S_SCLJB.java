package com.xunjia.pes.bizData.waterTreatment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("机泵基础信息")
@TableName("dmgc_s_scljb")
@Data
public class DMGC_S_SCLJB extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("站内编号")
    @TableField
    private String znbh;

    @ApiModelProperty("泵类型")
    @TableField
    private String blx;

    @ApiModelProperty("泵型号")
    @TableField
    private String bxh;

    @ApiModelProperty("投用日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date tcrq;

    @ApiModelProperty("生产厂家")
    @TableField
    private String sccj;

    @ApiModelProperty("额定扬程")
    @TableField
    private double edyc;

    @ApiModelProperty("额定流量")
    @TableField
    private double edll;

    @ApiModelProperty("电机功率")
    @TableField
    private double djgl;

    @ApiModelProperty("电机型号")
    @TableField
    private String djxh;

    @ApiModelProperty("电机生产厂家")
    @TableField
    private String djsccj;

    @ApiModelProperty("电机额定电流")
    @TableField
    private double djeddl;

    @ApiModelProperty("电机功率因数")
    @TableField
    private double djglys;

    @ApiModelProperty("电机效率")
    @TableField
    private double djxl;

    @ApiModelProperty("电机变频器编号")
    @TableField
    private String djbpqbh;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("所属站库ID")
    @TableField
    private String sszkid;

    @ApiModelProperty("所属站库表名称")
    @TableField
    private String sszkbmc;

    @ApiModelProperty("所属站名")
    @TableField
    private String sszkmc;

    @ApiModelProperty("生产日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date scrq;

    @ApiModelProperty("资产号")
    @TableField
    private String zch;

    @ApiModelProperty("额定转速")
    @TableField
    private double edzs;

    @ApiModelProperty("额定工作压力")
    @TableField
    private double edgzyl;

    @ApiModelProperty("工作介质")
    @TableField
    private String gzjz;

    @ApiModelProperty("主要技术参数")
    @TableField
    private String zyjscs;

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

    @ApiModelProperty("更换前eventId")
    @TableField("CEVENTID")
    private String cEventId;

//    @ApiModelProperty("所属站类型")
//    @TableField
//    private String zlx;
}
