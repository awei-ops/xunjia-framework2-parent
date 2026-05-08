package com.xunjia.pes.bizData.oil.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xunjia.framework.utils.excel.ExportColumn;
import com.xunjia.pes.bizData.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("脱水站基础信息")
@TableName("dmgc_y_tsz_new")
@Data
public class DMGC_Y_TSZ_NEW extends BaseEntity {

    @ApiModelProperty("名称")
    @TableField
    private String mc;

    @ApiModelProperty("站类型")
    @TableField
    private String zlx;

    @ApiModelProperty("来液类型")
    @TableField
    private String lylx;

    @ApiModelProperty("工艺流程")
    @TableField
    private String gylc;

    @ApiModelProperty("一段脱水工艺")
    @TableField
    private String ydtsfs;

    @ApiModelProperty("一段设计规模")
    @TableField
    private int ydsjgm;

    @ApiModelProperty("二段脱水工艺")
    @TableField
    private String edtsfs;

    @ApiModelProperty("二段设计规模")
    @TableField
    private int edsjgm;

    @ApiModelProperty("三段脱水工艺")
    @TableField
    private String sdtsfs;

    @ApiModelProperty("三段设计规模")
    @TableField
    private int sdsjgm;

    @ApiModelProperty("外输油量设计规模")
    @TableField
    private int wsylsjgm;

//    @ApiModelProperty("管辖接转站数")
//    @TableField
//    private int sgxyzs;

    @ApiModelProperty("直接管辖井数")
    @TableField
    private int yzlcyjs;

    @ApiModelProperty("原油密闭情况")
    @TableField
    private String yymbqk;

    @ApiModelProperty("采暖方式")
    @TableField
    private String cnfs;

    @ApiModelProperty("消防方式")
    @TableField
    private String xffs;

    @ApiModelProperty("总建筑面积")
    @TableField
    private int jzmj;

    @ApiModelProperty("主题建筑结构")
    @TableField
    private String jzjg;

    @ApiModelProperty("占地面积")
    @TableField
    private int zdmj;

    @ApiModelProperty("热负荷")
    @TableField
    private double rfh;

    @ApiModelProperty("装机负荷")
    @TableField
    private double zjfh;

    @ApiModelProperty("站场等级")
    @TableField
    private String zcdj;

    @ApiModelProperty("所属联合站名称")
    @TableField
    private String sslhzmc;

    @ApiModelProperty("下游污水站名称")
    @TableField
    private String xywszmc;

    @ApiModelProperty("下游脱水站名称")
    @TableField
    private String gtszmc;

    @ApiModelProperty("下游原稳站名称")
    @TableField
    private String gywzmc;

    @ApiModelProperty("下游油库名称")
    @TableField
    private String gykmc;

    @ApiModelProperty("外站来液端口名称")
    @TableField
    private String wzlydk;

    @ApiModelProperty("投产日期")
    @TableField
    private Date tcrq;

    @ApiModelProperty("是否数字化")
    @TableField
    private String sfszh;

    @ApiModelProperty("是否无人值守")
    @TableField
    private String sfwrzs;

    @ApiModelProperty("用工人数")
    @TableField
    private int ygrs;

    @ApiModelProperty("运行状态")
    @TableField
    private String sfbf;

    @ApiModelProperty("报废日期")
    @TableField
    private Date bfrq;

    @ApiModelProperty("周边环境")
    @TableField
    private String zbhj;

    @ApiModelProperty("地理位置")
    @TableField
    private String dlwz;

    @ApiModelProperty("油品")
    @TableField
    private String yp;

    @ApiModelProperty("下游污水站id")
    @TableField("XYWSZ_EVENTID")
    private String xywszEventId;

    @ApiModelProperty("下游脱水站id")
    @TableField("GTSZ_EVENTID")
    private String gtszEventId;

    @ApiModelProperty("下游原稳站id")
    @TableField("GYWZ_EVENTID")
    private String gywzEventId;

    @ApiModelProperty("下游油库id")
    @TableField("GYK_EVENTID")
    private String gykEventId;

    @ApiModelProperty("所属联合站id")
    @TableField("SSLHZ_EVENTID")
    private String sslhzEventId;

    @ApiModelProperty("所属单位代码")
    @TableField
    private Long ssdwdm;

    @ApiModelProperty("备注")
    @TableField
    private String bz;

    @ApiModelProperty("编码")
    @TableField
    private String code;

    @ApiModelProperty("作业区名称")
    @TableField
    private String zyqName;
}
