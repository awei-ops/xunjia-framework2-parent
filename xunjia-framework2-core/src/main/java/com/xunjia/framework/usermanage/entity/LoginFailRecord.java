package com.xunjia.framework.usermanage.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="f_login_fail_record")
@Getter
@Setter
public class LoginFailRecord {


    @ApiModelProperty(value="主键id")
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")
    private String id;

    @Column
    private String ip;

    @ApiModelProperty(value="账户登录失败次数")
    @Column
    private int loginFailCount;

    @ApiModelProperty(value="最后一次登录失败时间")
    @Column
    private LocalDateTime lastFailTime;

    @ApiModelProperty(value="下次允许登录的时间")
    @Column
    private LocalDateTime nextLoginTime;

}
