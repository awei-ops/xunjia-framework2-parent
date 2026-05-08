package com.xunjia.pes.bizData.waterInjection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("注水站生产动态日数据")
@TableName("dmgc_s_d_zszrsj")
@Data
public class DMGC_S_D_ZSZRSJ extends BaseEntity {

    @ApiModelProperty("站id")
    @TableField
    private String zid;

    @ApiModelProperty("站名称")
    @TableField
    @ExportColumn(name = "站名称", sort = 1)
    private String zmc;

    @ApiModelProperty("日期")
    @TableField
    @ExportColumn(name = "日期", sort = 2)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date rq;

    @ApiModelProperty("综合用电量")
    @TableField
    @ExportColumn(name = "综合用电量", sort = 3)
    private Double zhydl;

    @ApiModelProperty("综合单耗")
    @TableField
    @ExportColumn(name = "综合单耗", sort = 4)
    private Double zhdh;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("是否允许删除")
    @TableField("ALLOW_DELETE")
    private Double allowDelete;

    @ApiModelProperty("所属单位名称")
    @TableField(exist = false)
    @ExportColumn(name = "所属单位名称", sort = 5)
    private String ssdwName;

    @ApiModelProperty("综合单耗得分")
    @TableField
    private Double zhdhScore;

    @ApiModelProperty("综合单耗权重")
    @TableField
    private Double zhdhWeight;

    @ApiModelProperty("综合单耗权重得分")
    @TableField
    private Double zhdhWeightScore;

    @ApiModelProperty("综合单耗评价")
    @TableField
    private String zhdhPj;

    @ApiModelProperty("本站该日综合得分")
    @TableField
    private Double jxpjScore;

}
