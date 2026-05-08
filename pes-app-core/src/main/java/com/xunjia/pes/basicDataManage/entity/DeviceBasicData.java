package com.xunjia.pes.basicDataManage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel(value="装置基础数据指标")
@Entity
@Table(name="t_device_basic_data")
@Getter
@Setter
public class DeviceBasicData implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="站编码")
    @Column
    private String stationSystemCode;

    @ApiModelProperty(value="装置类型编码")
    @Column
    private String deviceTypeCode;

    @ApiModelProperty(value="评价指标名称")
    @Column
    private String evaluationIndexName;

    @ApiModelProperty(value="评价指标级别")
    @Column
    private String evaluationIndexLevel;

    @ApiModelProperty(value="权重")
    @Column
    private String weights;

    @ApiModelProperty(value="分类，泵（pump），加热炉（heatingFurnace）")
    @Column
    private String deviceCategory;

    @ApiModelProperty(value="删除标记")
    @Column
    private int deleteFlag;
}
