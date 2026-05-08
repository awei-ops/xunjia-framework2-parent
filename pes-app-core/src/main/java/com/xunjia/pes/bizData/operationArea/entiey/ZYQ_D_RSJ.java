package com.xunjia.pes.bizData.operationArea.entiey;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("作业区日数据（自建表）")
@TableName("zyq_d_rsj")
@Data
public class ZYQ_D_RSJ {

    @ApiModelProperty(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date rq;

    @ApiModelProperty("作业区名称")
    @TableField
    private String zyqName;

    @ApiModelProperty("作业区得分")
    @TableField
    private Double zyqScore;
}
