package com.xunjia.framework.device.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.xunjia.framework.usermanage.entity.Organization;
import org.hibernate.annotations.GenericGenerator;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="f_device")
@Getter
@Setter
public class Device implements Serializable{
	
	private static final long serialVersionUID = -7104757682458016746L;
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	@Column(name = "id", unique = true, nullable = false)
	private String id;
	
	@Column
	private String name;
	
	@Column
	private String code;
	
	@Column
	private Integer orderNo;

	@Column
	private Integer enabled;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "orgId")
	private Organization org;

}
