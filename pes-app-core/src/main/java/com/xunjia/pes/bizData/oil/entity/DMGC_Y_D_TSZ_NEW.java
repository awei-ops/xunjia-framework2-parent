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

@ApiModel("脱水站生产动态日数据")
@TableName("dmgc_y_d_tsz_new")
@Data
public class DMGC_Y_D_TSZ_NEW extends BaseEntity {

    @ApiModelProperty("日期")
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExportColumn(name = "日期", sort = 2)
    private Date rq;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("稳前供油量")
    @TableField
    @ExportColumn(name = "稳前供油量", sort = 3)
    private int wqgyl;

    @ApiModelProperty("稳后供油量")
    @TableField
    @ExportColumn(name = "稳后供油量", sort = 4)
    private int whhyl;

    @ApiModelProperty("外输油量")
    @TableField
    @ExportColumn(name = "外输油量", sort = 5)
    private int wsyl;

    @ApiModelProperty("外输油密度")
    @TableField
    @ExportColumn(name = "外输油密度", sort = 6)
    private Double wsymd;

    @ApiModelProperty("外输油质量")
    @TableField
    @ExportColumn(name = "外输油质量", sort = 7)
    private int wsyzl;

    @ApiModelProperty("老化油回收量")
    @TableField
    @ExportColumn(name = "老化油回收量", sort = 8)
    private int lhyhsl;

    @ApiModelProperty("自耗油量")
    @TableField
    @ExportColumn(name = "自耗油量", sort = 9)
    private int zhyl;

    @ApiModelProperty("外输油含水率")
    @TableField
    @ExportColumn(name = "外输油含水率", sort = 10)
    private Double wsyhsl;

    @ApiModelProperty("外供污水量")
    @TableField
    @ExportColumn(name = "外供污水量", sort = 11)
    private int wgwsl;

    @ApiModelProperty("污水含油量")
    @TableField
    @ExportColumn(name = "污水含油量", sort = 12)
    private Double wshyl;

    @ApiModelProperty("掺水量")
    @TableField
    @ExportColumn(name = "掺水量", sort = 13)
    private int csl;

    @ApiModelProperty("掺水压力")
    @TableField
    @ExportColumn(name = "掺水压力", sort = 14)
    private Double csyl;

    @ApiModelProperty("掺水温度")
    @TableField
    @ExportColumn(name = "掺水温度", sort = 15)
    private int cswd;

    @ApiModelProperty("外输气量")
    @TableField
    @ExportColumn(name = "外输气量", sort = 16)
    private int wsql;

    @ApiModelProperty("返输气量")
    @TableField
    @ExportColumn(name = "返输气量", sort = 17)
    private int fsql;

    @ApiModelProperty("耗气量")
    @TableField
    @ExportColumn(name = "耗气量", sort = 18)
    private int hql;

    @ApiModelProperty("外输气是否至处理站")
    @TableField
    @ExportColumn(name = "外输气是否至处理站", sort = 19)
    private String wsqsfzclz;

    @ApiModelProperty("外输天然气压力")
    @TableField
    @ExportColumn(name = "外输天然气压力", sort = 20)
    private Double wstrqyl;

    @ApiModelProperty("输油耗电量")
    @TableField
    @ExportColumn(name = "输油耗电量", sort = 21)
    private int syhdl;

    @ApiModelProperty("掺水耗电量")
    @TableField
    @ExportColumn(name = "掺水耗电量", sort = 22)
    private int cshdl;

    @ApiModelProperty("综合耗电量")
    @TableField
    @ExportColumn(name = "综合耗电量", sort = 23)
    private int zhhdl;

    @ApiModelProperty("来油汇管温度")
    @TableField
    @ExportColumn(name = "来油汇管温度", sort = 24)
    private int lyhgwd;

    @ApiModelProperty("输油温度")
    @TableField
    @ExportColumn(name = "输油温度", sort = 25)
    private int sywd;

    @ApiModelProperty("脱水温度")
    @TableField
    @ExportColumn(name = "脱水温度", sort = 26)
    private int tswd;

    @ApiModelProperty("输油汇管压力")
    @TableField
    @ExportColumn(name = "输油汇管压力", sort = 27)
    private Double syhgyl;

    @ApiModelProperty("破乳剂用量")
    @TableField
    @ExportColumn(name = "破乳剂用量", sort = 28)
    private Double prjyl;

    @ApiModelProperty("阻垢剂用量")
    @TableField
    @ExportColumn(name = "阻垢剂用量", sort = 29)
    private Double zgjyl;

    @ApiModelProperty("净水剂用量")
    @TableField
    @ExportColumn(name = "净水剂用量", sort = 30)
    private Double jsjyl;

//    @ApiModelProperty("流动改进剂用量")
//    @TableField
//    private Double ldgsjyl;

    @ApiModelProperty("其他药剂用量")
    @TableField
    @ExportColumn(name = "其他药剂用量", sort = 31)
    private Double qtyjyl;

    @ApiModelProperty("输油系统效率")
    @TableField
    @ExportColumn(name = "输油系统效率", sort = 32)
    private Double syxtxl;

    @ApiModelProperty("吨液耗电")
    @TableField
    @ExportColumn(name = "吨液耗电", sort = 33)
    private Double dyehd;

    @ApiModelProperty("吨液耗电得分")
    @TableField
    private Double dyehdScore;
    @ApiModelProperty("吨液耗电权重")
    @TableField
    private Double dyehdWeight;
    @ApiModelProperty("吨液耗电权重得分")
    @TableField
    private Double dyehdWeightScore;
    @ApiModelProperty("吨液耗电评价")
    @TableField
    private String dyehdPj;

    @ApiModelProperty("吨油耗电")
    @TableField
    @ExportColumn(name = "吨油耗电", sort = 34)
    private Double dyohd;

    @ApiModelProperty("吨油耗电得分")
    @TableField
    private Double dyohdScore;
    @ApiModelProperty("吨油耗电权重")
    @TableField
    private Double dyohdWeight;
    @ApiModelProperty("吨油耗电权重得分")
    @TableField
    private Double dyohdWeightScore;
    @ApiModelProperty("吨油耗电评价")
    @TableField
    private String dyohdPj;

    @ApiModelProperty("吨液耗气")
    @TableField
    @ExportColumn(name = "吨液耗气", sort = 35)
    private Double dyehq;

    @ApiModelProperty("吨液耗气得分")
    @TableField
    private Double dyehqScore;
    @ApiModelProperty("吨液耗气权重")
    @TableField
    private Double dyehqWeight;
    @ApiModelProperty("吨液耗气权重得分")
    @TableField
    private Double dyehqWeightScore;
    @ApiModelProperty("吨液耗气评价")
    @TableField
    private String dyehqPj;

    @ApiModelProperty("吨油耗气")
    @TableField
    @ExportColumn(name = "吨油耗气", sort = 36)
    private Double dyohq;

    @ApiModelProperty("吨油耗气得分")
    @TableField
    private Double dyohqScore;
    @ApiModelProperty("吨油耗气权重")
    @TableField
    private Double dyohqWeight;
    @ApiModelProperty("吨油耗气权重得分")
    @TableField
    private Double dyohqWeightScore;
    @ApiModelProperty("吨油耗气评价")
    @TableField
    private String dyohqPj;

    @ApiModelProperty("综合单耗")
    @TableField
    @ExportColumn(name = "综合单耗", sort = 37)
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

    @ApiModelProperty("运行评价")
    @TableField
    @ExportColumn(name = "运行评价", sort = 38)
    private String yxpj;

    @ApiModelProperty("备注")
    @TableField
    @ExportColumn(name = "备注", sort = 39)
    private String bz;

    @ApiModelProperty("站ID")
    @TableField
    private String zid;

    @ApiModelProperty("转油放水站数")
    @TableField
    @ExportColumn(name = "转油放水站数", sort = 40)
    private int tszs;

    @ApiModelProperty("总井数")
    @TableField
    @ExportColumn(name = "总井数", sort = 41)
    private int zjs;

    @ApiModelProperty("开井数")
    @TableField
    @ExportColumn(name = "开井数", sort = 42)
    private int kjs;

    @ApiModelProperty("允许删除")
    @TableField("ALLOW_DELETE")
    private int allowDelete;

    @ApiModelProperty("一段含水率")
    @TableField
    @ExportColumn(name = "一段含水率", sort = 43)
    private Double ydhsl;

    @ApiModelProperty("二段含水率")
    @TableField
    @ExportColumn(name = "二段含水率", sort = 44)
    private Double edhsl;

    @ApiModelProperty("三段含水率")
    @TableField
    @ExportColumn(name = "三段含水率", sort = 45)
    private Double sdhsl;

    @ApiModelProperty("一段处理量")
    @TableField
    @ExportColumn(name = "一段处理量", sort = 46)
    private Double ydcll;

    @ApiModelProperty("二段处理量")
    @TableField
    @ExportColumn(name = "二段处理量", sort = 47)
    private Double edcll;

    @ApiModelProperty("三段处理量")
    @TableField
    @ExportColumn(name = "三段处理量", sort = 48)
    private Double sdcll;

    @ApiModelProperty("一段负荷率")
    @TableField
    @ExportColumn(name = "一段负荷率", sort = 49)
    private Double ydfhl;

    @ApiModelProperty("二段负荷率")
    @TableField
    @ExportColumn(name = "二段负荷率", sort = 50)
    private Double edfhl;

    @ApiModelProperty("三段负荷率")
    @TableField
    @ExportColumn(name = "三段负荷率", sort = 51)
    private Double sdfhl;

    @ApiModelProperty("预脱水剂用量")
    @TableField
    @ExportColumn(name = "预脱水剂用量", sort = 52)
    private Double ytsjyl;

    @ApiModelProperty("反向破乳剂用量")
    @TableField
    @ExportColumn(name = "反向破乳剂用量", sort = 53)
    private Double fxpljyl;

    @ApiModelProperty("事故罐库存")
    @TableField
    @ExportColumn(name = "事故罐库存", sort = 54)
    private int sggkc;

    @ApiModelProperty("沉降罐库存")
    @TableField
    @ExportColumn(name = "沉降罐库存", sort = 55)
    private int cjgkc;

    @ApiModelProperty("")
    @TableField
    private Double ylstchsl;

    @ApiModelProperty("")
    @TableField
    private Double dtshsl;

    @ApiModelProperty("")
    @TableField
    private int ylstccll;

    @ApiModelProperty("")
    @TableField
    private int dtscll;

    @ApiModelProperty("")
    @TableField
    private int hysbcll;

    @ApiModelProperty("")
    @TableField
    private Double ylstcfhl;

    @ApiModelProperty("")
    @TableField
    private Double dtsfhl;

    @ApiModelProperty("")
    @TableField
    private Double hysbfhl;

    @ApiModelProperty("")
    @TableField
    private String wzlydk;

    @ApiModelProperty("")
    @TableField
    private int wzlyl;

    @ApiModelProperty("")
    @TableField
    private Double wzlyhsl;

    @ApiModelProperty("")
    @TableField
    private int wzlywd;

    @ApiModelProperty("")
    @TableField
    private Double wzlyyl;

    @ApiModelProperty("站名称")
    @ExportColumn(name = "站名称", sort = 1)
    private String stationName;

    @ApiModelProperty("本站该日综合得分")
    @TableField
    private Double jxpjScore;

}
