package com.xunjia.pes.bizData.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("监测项目与指标要求类型")
@TableName("t_requirements")
@Data
public class Requirements {
    @ApiModelProperty(value = "id")
    @TableId
    private String id;
    @ApiModelProperty("监测项目与指标要求名称")
    @TableField
    private String name;

    @ApiModelProperty("指标/标杆")
    @TableField
    private String type;

    @ApiModelProperty("排序")
    @TableField
    private Integer orderNo;
}
