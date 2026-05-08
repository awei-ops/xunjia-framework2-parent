package com.xunjia.pes.basicDataManage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel(value="原油集输系统指标体系、注入系统指标体系、水处理系统指标体系")
@Entity
@Table(name="t_professional_system")
@Getter
@Setter
public class ProfessionalSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="作业区编码")
    @Column
    private String workAreaCode;

    @ApiModelProperty(value="专业系统编码")
    @Column
    private String professionalSystemCode;

    @ApiModelProperty(value="专业系统名称")
    @Column
    private String professionalSystemName;

    @ApiModelProperty(value="评价指标名称")
    @Column
    private String evaluationIndexName;

    @ApiModelProperty(value="评价指标级别")
    @Column
    private String evaluationIndexLevel;

    @ApiModelProperty(value="权重")
    @Column
    private String weights;

    @ApiModelProperty(value="原油集输系统指标体系(crudeOil)、注入系统指标体系(injection)、水处理系统指标体系(water)")
    @Column
    private String professionalType;

    @ApiModelProperty(value="删除标记")
    @Column
    private int deleteFlag;
}
