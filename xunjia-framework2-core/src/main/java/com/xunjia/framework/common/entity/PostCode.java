package com.xunjia.framework.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ApiModel("行政区域码")
@Entity
@Table(name = "b_postcode")
@Getter
@Setter
@ToString
public class PostCode {

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String province;

    @Column
    private String city;

    @Column
    private String area;

    @Column(name="post_code")
    private String postCode;

    @Column(name="area_code")
    private String areaCode;
}
