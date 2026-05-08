package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("权重类别基础信息")
@TableName("t_indicator_type_info")
@Data
public class IndicatorTypeInfo {
    @ApiModelProperty(value = "id")
    @TableId
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
    @ApiModelProperty("指标级别排序")
    @TableField
    private Integer orderNo;
}
