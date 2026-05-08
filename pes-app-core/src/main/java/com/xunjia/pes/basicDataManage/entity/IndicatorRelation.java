package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("权重类型与项目关系")
@TableName("t_indicator_relation")
@Data
public class IndicatorRelation {
    @ApiModelProperty(value = "id")
    @TableId
    private Integer id;

    @ApiModelProperty(value = "indicatorTypeInfoId")
    @TableField
    private Integer indicatorTypeInfoId;

    @ApiModelProperty(value = "indicatorItemId")
    @TableField
    private Integer indicatorItemId;

    @ApiModelProperty("权重类别信息")
    @TableField(exist = false)
    private IndicatorTypeInfo indicatorTypeInfo;
    @ApiModelProperty("权重项目得分")
    @TableField(exist = false)
    private IndicatorItem indicatorItem;
}
