package com.xunjia.framework.usermanage.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="f_font_icon")
@Getter
@Setter
public class FontIcon implements Serializable {

    @ApiModelProperty(value="主键id",example="1")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @ApiModelProperty(value="图标代码，css类名",example="fa fa-plus")
    @Column
    private String code;

    @ApiModelProperty(value="图标分类")
    @Column
    private String typeName;

    @ApiModelProperty(value="图标分类编码")
    @Column
    private String typeCode;

}
