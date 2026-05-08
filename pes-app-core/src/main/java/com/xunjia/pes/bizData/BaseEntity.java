package com.xunjia.pes.bizData;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    @ApiModelProperty("id")
    @TableId("EVENTID")
    private String eventId;

    @ApiModelProperty("数据状态")
    @TableField("DATA_STATE")
    private String dataState;

    @ApiModelProperty("创建时间")
    @TableField("CREATE_DATE")
    private Date createDate;

    @ApiModelProperty("创建人ID")
    @TableField("CREATE_USER_ID")
    private String createUserId;

    @ApiModelProperty("修改时间")
    @TableField("UPDATE_DATE")
    private Date updateDate;

    @ApiModelProperty("修改人ID")
    @TableField("UPDATE_USER_ID")
    private String updateUserId;

    @ApiModelProperty("创建单位标识")
    @TableField("CREATE_SSDWBS")
    private Long createSsdwbs;

//    @ApiModelProperty("所属站库主键")
//    @TableField("SSZK_EVENTID")
//    private String sszkEventId;
}
