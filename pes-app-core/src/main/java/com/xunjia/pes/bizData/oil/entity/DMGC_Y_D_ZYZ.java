package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("转油放水站生产动态日数据")
@TableName("dmgc_y_d_zyz")
@Data
public class DMGC_Y_D_ZYZ extends BaseEntity {

    @ApiModelProperty("站库主键")
    @TableField("ZK_EVENTID")
    private String zkEventId;

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ExportColumn(name = "日期", sort = 2)
    private Date rq;

    @ApiModelProperty("外站来液量")
    @TableField
    @ExportColumn(name = "外站来液量", sort = 3)
    private int wzlyl;

    @ApiModelProperty("处理量")
    @TableField
    @ExportColumn(name = "处理量", sort = 4)
    private Integer cll;

    @ApiModelProperty("外输液量")
    @TableField
    @ExportColumn(name = "外输液量", sort = 5)
    private int wsyl;

    @ApiModelProperty("外输液质量")
    @TableField
    @ExportColumn(name = "外输液质量", sort = 6)
    private int wsyzl;

    @ApiModelProperty("外输水量")
    @TableField
    @ExportColumn(name = "外输水量", sort = 7)
    private int wssl;

    @ApiModelProperty("掺水量")
    @TableField
    @ExportColumn(name = "掺水量", sort = 8)
    private Double csl;

    @ApiModelProperty("掺稀油量")
    @TableField
    @ExportColumn(name = "掺稀油量", sort = 9)
    private int cxyl;

    @ApiModelProperty("掺稀油温度")
    @TableField
    @ExportColumn(name = "掺稀油温度", sort = 10)
    private int cxywd;

    @ApiModelProperty("掺稀油压力")
    @TableField
    @ExportColumn(name = "掺稀油压力", sort = 11)
    private Double cxyyl;

    @ApiModelProperty("热洗水量")
    @TableField
    @ExportColumn(name = "热洗水量", sort = 12)
    private int rxsl;

    @ApiModelProperty("外输液含水率")
    @TableField
    @ExportColumn(name = "外输液含水率", sort = 13)
    private Double wsyhsl;

    @ApiModelProperty("外输液负荷率")
    @TableField
    @ExportColumn(name = "外输液负荷率", sort = 14)
    private Double wsyfhl;

    @ApiModelProperty("外输液密度")
    @TableField
    @ExportColumn(name = "外输液密度", sort = 15)
    private Double wsymd;

    @ApiModelProperty("外输水负荷率")
    @TableField
    @ExportColumn(name = "外输水负荷率", sort = 16)
    private Double wssfhl;

    @ApiModelProperty("外输气量")
    @TableField
    @ExportColumn(name = "外输气量", sort = 17)
    private int wsql;

    @ApiModelProperty("耗气量")
    @TableField
    @ExportColumn(name = "耗气量", sort = 18)
    private Integer hql;

    @ApiModelProperty("返输气量")
    @TableField
    @ExportColumn(name = "返输气量", sort = 19)
    private int fsql;

    @ApiModelProperty("输油耗电量")
    @TableField
    @ExportColumn(name = "输油耗电量", sort = 20)
    private int syhdl;

    @ApiModelProperty("输水耗电量")
    @TableField
    @ExportColumn(name = "输水耗电量", sort = 21)
    private int sshdl;

    @ApiModelProperty("掺水耗电量")
    @TableField
    @ExportColumn(name = "掺水耗电量", sort = 22)
    private int cshdl;

    @ApiModelProperty("热洗耗电量")
    @TableField
    @ExportColumn(name = "热洗耗电量", sort = 23)
    private int rxhdl;

    @ApiModelProperty("综合耗电量")
    @TableField
    @ExportColumn(name = "综合耗电量", sort = 24)
    private Integer zhhdl;

    @ApiModelProperty("吨液耗电")
    @TableField
    @ExportColumn(name = "吨液耗电", sort = 25)
    private Double dyhd;

    @ApiModelProperty("吨液耗电得分")
    @TableField
    private Double dyhdScore;

    @ApiModelProperty("吨液耗电权重")
    @TableField
    private Double dyhdWeight;

    @ApiModelProperty("吨液耗电权重得分")
    @TableField
    private Double dyhdWeightScore;

    @ApiModelProperty("吨液耗电评价")
    @TableField
    private String dyhdPj;

    @ApiModelProperty("吨液耗气")
    @TableField
    @ExportColumn(name = "吨液耗气", sort = 26)
    private Double dyhq;

    @ApiModelProperty("吨液耗气得分")
    @TableField
    private Double dyhqScore;

    @ApiModelProperty("吨液耗气权重")
    @TableField
    private Double dyhqWeight;

    @ApiModelProperty("吨液耗气权重得分")
    @TableField
    private Double dyhqWeightScore;

    @ApiModelProperty("吨液耗气评价")
    @TableField
    private String dyhqPj;

    @ApiModelProperty("综合单耗")
    @TableField
    @ExportColumn(name = "综合单耗", sort = 27)
    private Double zhdh;

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

    @ApiModelProperty("输水单耗")
    @TableField
    @ExportColumn(name = "输水单耗", sort = 28)
    private Double ssdh;

    @ApiModelProperty("本站来液温度")
    @TableField
    @ExportColumn(name = "本站来液温度", sort = 29)
    private int bzlywd;

    @ApiModelProperty("外站来液温度")
    @TableField
    @ExportColumn(name = "外站来液温度", sort = 30)
    private int wzlywd;

    @ApiModelProperty("输油温度")
    @TableField
    @ExportColumn(name = "输油温度", sort = 31)
    private int sywd;

    @ApiModelProperty("掺水出站温度")
    @TableField
    @ExportColumn(name = "掺水出站温度", sort = 32)
    private int csczwd;

    @ApiModelProperty("掺水出站压力")
    @TableField
    @ExportColumn(name = "掺水出站压力", sort = 33)
    private Double csczyl;

    @ApiModelProperty("输油汇管压力")
    @TableField
    @ExportColumn(name = "输油汇管压力", sort = 34)
    private Double syhgyl;

    @ApiModelProperty("破乳剂用量")
    @TableField
    @ExportColumn(name = "破乳剂用量", sort = 35)
    private Double rprjyl;

    @ApiModelProperty("阻垢剂用量")
    @TableField
    @ExportColumn(name = "阻垢剂用量", sort = 36)
    private Double rzgjyl;

    @ApiModelProperty("流动改进剂用量")
    @TableField
    @ExportColumn(name = "流动改进剂用量", sort = 37)
    private Double rldgjjyl;

    @ApiModelProperty("其他药剂用量")
    @TableField
    @ExportColumn(name = "其他药剂用量", sort = 38)
    private Double qtyjyl;

    @ApiModelProperty("输油系统效率")
    @TableField
    @ExportColumn(name = "输油系统效率", sort = 39)
    private Double syxtxl;

    @ApiModelProperty("运行评价")
    @TableField
    @ExportColumn(name = "运行评价", sort = 40)
    private String yxpj;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 41)
    private String bz;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("总井数")
    @TableField
    @ExportColumn(name = "总井数", sort = 42)
    private int zjs;

    @ApiModelProperty("开井数")
    @TableField
    @ExportColumn(name = "开井数", sort = 43)
    private int kjs;

    @ApiModelProperty("新开井数")
    @TableField
    @ExportColumn(name = "新开井数", sort = 44)
    private int xkjs;

    @ApiModelProperty("新关井数")
    @TableField
    @ExportColumn(name = "新关井数", sort = 45)
    private int xgjs;

    @ApiModelProperty("井口平均回压")
    @TableField
    @ExportColumn(name = "井口平均回压", sort = 46)
    private Double jkpjhy;

    @ApiModelProperty("井口产液量")
    @TableField
    @ExportColumn(name = "井口产液量", sort = 47)
    private Double jkcyl;

    @ApiModelProperty("热洗井数")
    @TableField
    @ExportColumn(name = "热洗井数", sort = 48)
    private int rxjs;

    @ApiModelProperty("掺水井数")
    @TableField
    @ExportColumn(name = "掺水井数", sort = 49)
    private int csjs;

    @ApiModelProperty("允许删除")
    @TableField("ALLOW_DELETE")
    private int allowDelete;

    @ApiModelProperty("热洗温度")
    @TableField
    @ExportColumn(name = "热洗温度", sort = 50)
    private int rxwd;

    @ApiModelProperty("热洗压力")
    @TableField
    @ExportColumn(name = "热洗压力", sort = 51)
    private Double rxyl;

    @ApiModelProperty("注汽锅炉排污量")
    @TableField
    @ExportColumn(name = "注汽锅炉排污量", sort = 52)
    private int zqglpwl;

    @ApiModelProperty("外站来油量")
    @TableField
    @ExportColumn(name = "外站来油量", sort = 53)
    private Double wzlyoul;

    @ApiModelProperty("外输油量")
    @TableField
    @ExportColumn(name = "外输油量", sort = 54)
    private Double wsyoul;

    @ApiModelProperty("站名称")
    @TableField
    @ExportColumn(name = "站名称", sort = 1)
    private String stationName;

    @ApiModelProperty("本站该日综合得分")
    @TableField
    private Double jxpjScore;
}
