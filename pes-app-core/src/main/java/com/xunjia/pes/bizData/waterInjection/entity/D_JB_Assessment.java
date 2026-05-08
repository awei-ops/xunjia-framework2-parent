package com.xunjia.pes.bizData.waterInjection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("注水泵泵效考核")
@TableName("d_jb_assessment")
@Data
public class D_JB_Assessment {

    @ApiModelProperty(value = "id")
    @TableId
    private String id;

    @ApiModelProperty("机泵id")
    @TableField
    private String jbId;

    @ApiModelProperty("机泵名称")
    @TableField
    private String jbName;

    @ApiModelProperty("注水站Id")
    @TableField
    private String zszId;

    @ApiModelProperty("注水站名称")
    @TableField
    private String zszName;

    @ApiModelProperty("日期")
    @TableField
    private Date rq;

    @ApiModelProperty("机泵类型")
    @TableField
    private String jbType;

    @ApiModelProperty("泵水量")
    @TableField
    private double bxl;

    @ApiModelProperty("用电量")
    @TableField
    private double ydl;

    @ApiModelProperty("泵水单耗")
    @TableField
    private double bsdh;

    @ApiModelProperty("运行状态")
    @TableField
    private String yxzt;

    @ApiModelProperty("流量")
    @TableField
    private double ll;

    @ApiModelProperty("节流损失率")
    @TableField
    private double jlssl;

    @ApiModelProperty("泵效")
    @TableField
    private double bx;

    @ApiModelProperty("泵运行状态区分")
    @TableField
    private String runningState;

    @ApiModelProperty("对应标准")
    @TableField
    private String dybz;

    @ApiModelProperty("对应分值")
    @TableField
    private double dyfz;

    @ApiModelProperty("泵效评价")
    @TableField
    private String bxpj;

    @ApiModelProperty("节流损失偏大")
    @TableField
    private String throttlingLoss;

    @ApiModelProperty("损失分值")
    @TableField
    private double sslfz;

    @ApiModelProperty("损失评价")
    @TableField
    private String sspj;
}
