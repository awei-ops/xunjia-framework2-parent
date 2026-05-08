package com.xunjia.pes.basicDataManage.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommonEntity {
    @ApiModelProperty(value="标签")
    private String label;
    @ApiModelProperty(value="值")
    private String value;
}
