package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("权重记录")
@TableName("t_indicators")
@Data
public class Indicators {
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("指标分类名称")
    @TableField
    private String typeName;
    @ApiModelProperty("指标分类编码")
    @TableField
    private String typeCode;
    @ApiModelProperty("指标级别名称")
    @TableField
    private String levelName;
    @ApiModelProperty("指标级别编码")
    @TableField
    private String levelCode;
    @ApiModelProperty("指标项目名称")
    @TableField
    private String itemName;
    @ApiModelProperty("指标项目编码")
    @TableField
    private String itemCode;
    @ApiModelProperty("指标权重")
    @TableField
    private double weight;
    @ApiModelProperty(value = "删除标记")
    @TableField
    private Integer deleteFlag;
}
