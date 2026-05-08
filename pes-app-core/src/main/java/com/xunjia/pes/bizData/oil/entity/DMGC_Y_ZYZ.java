package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("接转（放水）站基础信息")
@TableName("dmgc_y_zyz")
@Data
public class DMGC_Y_ZYZ extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("站类型")
    @TableField
    private String zlx;

    @ApiModelProperty("集成状态")
    @TableField
    private String jczt;

    @ApiModelProperty("所属联合站名称")
    @TableField
    private String sslhzmc;

    @ApiModelProperty("输液下游站")
    @TableField
    private String symz;

    @ApiModelProperty("输水下游站")
    @TableField
    private String ssmz;

    @ApiModelProperty("输气下游站")
    @TableField
    private String sqmz;

    @ApiModelProperty("工艺流程")
    @TableField
    private String gylc;

    @ApiModelProperty("输液量设计规模")
    @TableField
    private int sylsjgm;

    @ApiModelProperty("处理量设计规模")
    @TableField
    private int cllsjgm;

    @ApiModelProperty("输水量设计规模")
    @TableField
    private String wsssfs;

    @ApiModelProperty("外输输送方式")
    @TableField
    private int sjjzfzs;

    @ApiModelProperty("设计进站阀组数")
    @TableField
    private int gxjljs;

    @ApiModelProperty("管辖计量站数")
    @TableField
    private int gxfzs;

    @ApiModelProperty("燃料类型")
    @TableField
    private String rllx;

    @ApiModelProperty("消防方式")
    @TableField
    private String xffs;

    @ApiModelProperty("采暖方式")
    @TableField
    private String cnfs;

//    @ApiModelProperty("总建筑面积")
//    @TableField
//    private int zjzmj;

//    @ApiModelProperty("主体建筑结构")
//    @TableField
//    private String ztjzjg;

    @ApiModelProperty("占地面积")
    @TableField
    private int zdmj;

    @ApiModelProperty("装机负荷")
    @TableField
    private double zjfh;

    @ApiModelProperty("热负荷")
    @TableField
    private double rfh;

    @ApiModelProperty("站场等级")
    @TableField
    private String zcdj;

    @ApiModelProperty("投产日期")
    @TableField
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
    private String sfbf;

    @ApiModelProperty("报废日期")
    @TableField
    private Date bfrq;

    @ApiModelProperty("周边环境")
    @TableField
    private String zbhj;

    @ApiModelProperty("油品")
    @TableField
    private String yp;

    @ApiModelProperty("地理位置")
    @TableField
    private String dlwz;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("编码")
    @TableField
    private String code;

    @ApiModelProperty("所属联合站ID")
    @TableField
    private String sslhzid;

    @ApiModelProperty("作业区名称")
    @TableField
    private String zyqName;
}
