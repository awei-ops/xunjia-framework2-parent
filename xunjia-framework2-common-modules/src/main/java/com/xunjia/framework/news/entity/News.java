package com.xunjia.framework.news.entity;

import java.io.Serializable;
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

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name="t_news")
@Data
public class News implements Serializable {
	
	private static final long serialVersionUID = -6186758955664041703L;
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",strategy="uuid")
	@Column(name = "id", unique = true, nullable = false)
	private String id;
	
	@Column
	private String title;
	
	@Column
	private String subTitle;
	
	@Column
	private Date publishDate;
	
	@Column
	private String author;
	
	@Column
	private int readCount;
	
	@Column
	private int auditState;
	
	@Column
	private int subTitlePos;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "typeId")
	private NewsType type;

}
