package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("节能措施详细")
@TableName("t_energy_saving_detail")
@Data
public class EnergySavingDetail {
    @ApiModelProperty(value = "id")
    @TableId
    private int id;

    @ApiModelProperty(value = "措施分类编码")
    @TableField
    private String measuresTypeCode;
    @ApiModelProperty(value = "措施细则分类编码")
    @TableField
    private String classificationCode;

    @ApiModelProperty(value = "措施内容")
    @TableField
    private String detail;
    @ApiModelProperty(value = "措施序号")
    @TableField
    private int orderNo;
}
