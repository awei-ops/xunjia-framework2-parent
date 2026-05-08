package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExamineColumn;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("站掺水泵生产动态日数据")
@TableName("dmgc_y_d_csb")
@Data
public class DMGC_Y_D_CSB extends BaseEntity {

    @ApiModelProperty("站名")
    @TableField
    @ExamineColumn(name = "站名称", sort = 1)
    private String zm;

    @ApiModelProperty("设备名称")
    @TableField("EQUIP_NAME")
    @ExamineColumn(name = "设备名称", sort = 2)
    private String equipName;

    @ApiModelProperty("站内编号")
    @TableField
    @ExamineColumn(name = "站内编号", sort = 3)
    private String znbh;

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ExamineColumn(name = "日期", sort = 4)
    private Date rq;

    @ApiModelProperty("泵效率")
    @TableField
    @ExamineColumn(name = "泵效率(%)", sort = 5)
    private Double bxl;

    @ApiModelProperty("泵效率得分")
    @TableField
    @ExamineColumn(name = "泵效率得分", sort = 6)
    private Double bxlScore;

    @ApiModelProperty("泵效率权重")
    @TableField
    @ExamineColumn(name = "泵效率权重", sort = 7)
    private Double bxlWeight;

    @ApiModelProperty("泵效率权重得分")
    @TableField
    @ExamineColumn(name = "泵效率权重得分", sort = 8)
    private Double bxlWeightScore;

    @ApiModelProperty("泵效率评价")
    @TableField
    @ExamineColumn(name = "泵效率评价", sort = 9)
    private String bxlPj;

    @ApiModelProperty("节流损失率")
    @TableField
    @ExamineColumn(name = "节流损失率(%)", sort = 10)
    private Double jlssl;

    @ApiModelProperty("节流损失率得分")
    @TableField
    @ExamineColumn(name = "节流损失率得分", sort = 11)
    private Double jlsslScore;

    @ApiModelProperty("节流损失率权重")
    @TableField
    @ExamineColumn(name = "节流损失率权重", sort = 12)
    private Double jlsslWeight;

    @ApiModelProperty("节流损失率权重得分")
    @TableField
    @ExamineColumn(name = "节流损失率权重得分", sort = 13)
    private Double jlsslWeightScore;

    @ApiModelProperty("节流损失率评价")
    @TableField
    @ExamineColumn(name = "节流损失率评价", sort = 14)
    private String jlsslPj;

    @ApiModelProperty("回流率")
    @TableField
    @ExamineColumn(name = "回流率(%)", sort = 15)
    private Double hlRate;

    @ApiModelProperty("回流率得分")
    @TableField
    @ExamineColumn(name = "回流率得分", sort = 16)
    private Double hlRateScore;

    @ApiModelProperty("回流率权重")
    @TableField
    @ExamineColumn(name = "回流率权重", sort = 17)
    private Double hlRateWeight;

    @ApiModelProperty("回流率权重得分")
    @TableField
    @ExamineColumn(name = "回流率权重得分", sort = 18)
    private Double hlRateWeightScore;

    @ApiModelProperty("回流率评价")
    @TableField
    @ExamineColumn(name = "回流率评价", sort = 19)
    private String hlRatePj;

    @ApiModelProperty("负荷率")
    @TableField
    @ExamineColumn(name = "负荷率(%)", sort = 20)
    private Double fhl;

    @ApiModelProperty("负荷率得分")
    @TableField
    @ExamineColumn(name = "负荷率得分", sort = 21)
    private Double fhlScore;

    @ApiModelProperty("负荷率权重")
    @TableField
    @ExamineColumn(name = "负荷率权重", sort = 22)
    private Double fhlWeight;

    @ApiModelProperty("负荷率权重得分")
    @TableField
    @ExamineColumn(name = "负荷率权重得分", sort = 23)
    private Double fhlWeightScore;

    @ApiModelProperty("负荷率评价")
    @TableField
    @ExamineColumn(name = "负荷率评价", sort = 24)
    private String fhlPj;

    @ApiModelProperty("机泵得分")
    @TableField
    @ExamineColumn(name = "机泵得分", sort = 25)
    private Double jbScore;

    @ApiModelProperty("机泵评价")
    @TableField
    @ExamineColumn(name = "机泵评价", sort = 26)
    private String jbPj;

    @ApiModelProperty("机泵权重机泵按权重（额定功率）比")
    @TableField
    private Double jbWeight;

    @ApiModelProperty("机泵权重得分")
    @TableField
    private Double jbWeightScore;

    @ApiModelProperty("录入的数据是否已经审核")
    @TableField
    private Boolean dataAlreadyAudited;

    @ApiModelProperty("机泵运行状态")
    @TableField
    private String yxzt;

    @ApiModelProperty("所属站库主键")
    @TableField("SSZK_EVENTID")
    private String sszkEventId;

    @ApiModelProperty("机泵主键")
    @TableField("JB_EVENTID")
    private String jbEventId;
}
