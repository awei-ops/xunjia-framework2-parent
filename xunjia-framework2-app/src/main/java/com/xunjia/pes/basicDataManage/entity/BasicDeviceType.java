package com.xunjia.pes.basicDataManage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel(value="装置类型")
@Entity
@Table(name="t_basic_device_type")
@Getter
@Setter
public class BasicDeviceType implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="编码")
    @Column
    private String deviceTypeCode;

    @ApiModelProperty(value="名称")
    @Column
    private String deviceTypeName;

    @ApiModelProperty(value="分类，泵（pump），加热炉（heatingFurnace）")
    @Column
    private String deviceCategory;

    @ApiModelProperty(value="删除标记")
    @Column
    private int deleteFlag;
}
