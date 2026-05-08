package com.xunjia.pes.bizData.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("水处理站设备运行日数据")
@TableName("t_benchmark")
@Data
public class Benchmark {
    @ApiModelProperty(value = "id")
    @TableId
    private String id;
    @ApiModelProperty("标杆名称")
    @TableField
    private String name;
    @ApiModelProperty("标杆编码")
    @TableField
    private String code;
    @ApiModelProperty("标杆类别")
    @TableField
    private String type;
    @ApiModelProperty("线性标杆右侧对应值")
    @TableField
    private Double rightValue;

    @ApiModelProperty("线性标杆右侧对应评分")
    @TableField
    private Double rightScore;
    @ApiModelProperty("线性标杆左侧对应值")
    @TableField
    private Double leftValue;

    @ApiModelProperty("线性标杆左侧对应评分")
    @TableField
    private Double leftScore;

    @ApiModelProperty("最低分对应值")
    @TableField
    private Double minValue;
    @ApiModelProperty("最低分")
    @TableField
    private Double minScore;

    @ApiModelProperty("最高分对应值")
    @TableField
    private Double maxValue;
    @ApiModelProperty("最高分")
    @TableField
    private Double maxScore;
    @ApiModelProperty(value="删除标记")
    @TableField
    private Integer deleteFlag;
}
