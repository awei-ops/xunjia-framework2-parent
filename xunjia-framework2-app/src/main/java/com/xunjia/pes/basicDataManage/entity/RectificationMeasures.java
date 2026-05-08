package com.xunjia.pes.basicDataManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("整改措施")
@TableName("t_rectification_measures")
@Data
public class RectificationMeasures {
    @ApiModelProperty(value = "id")
    @TableId
    private String id;
    @ApiModelProperty("源数据日期")
    @TableField
    private Date originalDataDate;
    @ApiModelProperty("节能措施编码")
    @TableField
    private String measuresTypeCode;
    @ApiModelProperty("站Id")
    @TableField
    private String zid;
    @ApiModelProperty("站名称")
    @TableField
    private String zmc;
    @ApiModelProperty("设备Id")
    @TableField
    private String equipmentId;
    @ApiModelProperty("设备名称")
    @TableField
    private String equipmentName;
    @ApiModelProperty("整改措施")
    @TableField
    private String measures;
}
