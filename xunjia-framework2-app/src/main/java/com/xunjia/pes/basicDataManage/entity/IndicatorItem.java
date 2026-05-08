package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("权重项目")
@TableName("t_indicator_item")
@Data
public class IndicatorItem {
    @ApiModelProperty(value = "id")
    @TableId
    private Integer id;
    @ApiModelProperty("指标项目名称")
    @TableField
    private String itemName;
    @ApiModelProperty("指标项目编码")
    @TableField
    private String itemCode;
}
