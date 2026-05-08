package com.xunjia.pes.basicDataManage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel(value="转油（放水）站指标体系、脱水站指标体系、注水站指标体系、污水处理站指标体系")
@Entity
@Table(name="t_station_system")
@Getter
@Setter
public class StationSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="专业系统编码")
    @Column
    private String professionalSystemCode;

    @ApiModelProperty(value="站编码")
    @Column
    private String stationSystemCode;

    @ApiModelProperty(value="站名称")
    @Column
    private String stationSystemName;

    @ApiModelProperty(value="评价指标名称")
    @Column
    private String evaluationIndexName;

    @ApiModelProperty(value="评价指标级别")
    @Column
    private String evaluationIndexLevel;

    @ApiModelProperty(value="权重")
    @Column
    private String weights;

    @ApiModelProperty(value="转油（放水）站指标体系(oilTransfer)、脱水站指标体系(dehydration)、注水站指标体系(waterFlooding)、污水处理站指标体系(sewage)")
    @Column
    private String stationType;

    @ApiModelProperty(value="删除标记")
    @Column
    private int deleteFlag;
}
