package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("联合站基础信息")
@TableName("dmgc_y_lhz")
@Data
public class DMGC_Y_LHZ extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("包含处理单元")
    @TableField
    private String bhcldy;

    @ApiModelProperty("投产日期")
    @TableField
    private Date tcrq;

    @ApiModelProperty("占地面积")
    @TableField
    private int zdmj;

    @ApiModelProperty("是否集中监控")
    @TableField
    private String sfjzjk;

    @ApiModelProperty("总建筑面积")
    @TableField
    private int jzmj;

//    @ApiModelProperty("用工人数")
//    @TableField
//    private int ygrs;

    @ApiModelProperty("运行状态")
    @TableField
    private String sfbf;

    @ApiModelProperty("周边环境")
    @TableField
    private String zbhj;

    @ApiModelProperty("报废日期")
    @TableField
    private Date bfrq;

    @ApiModelProperty("地理位置")
    @TableField
    private String dlwz;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("编码")
    @TableField
    private String code;
}
