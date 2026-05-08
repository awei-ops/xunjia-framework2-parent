package com.xunjia.framework.attachment.entity;

import java.util.Date;

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
import com.xunjia.framework.usermanage.entity.User;
import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="f_attachment")
@Getter
@Setter
public class Attachment {

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	@Column
	private String code;
	
	@Column
	private String extendName;
	
	@Column
	private String contentType;
	
	@Column
	private String originalFileName;
	
	@Column
	private String savePath;
	
	@Column
	private String title;
	
	@Column
	private String description;
	
	@Column
	private Date uploadTime;
	
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name = "uploadOrgId")
	private Organization uploadOrg;
	
	@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "uploadUserId")
	private User uploadUser;
	
	@Column
	private long downloadCount;
	
	@Column
	private String business;
	
	@Column
	private String businessId;
	
	/** 业务的子类型，如某业务包含照片、视频等不同类型的附件，利用此字段区分 */
	@Column
	private String businessSubType;
}
