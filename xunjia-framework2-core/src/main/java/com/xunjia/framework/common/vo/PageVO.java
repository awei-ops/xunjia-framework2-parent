package com.xunjia.framework.common.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页视图对象
 * 2020年6月10日
 * @author 姜浩
 */
@Getter
@Setter
public class PageVO<T> {
	
	public PageVO(){
		this.rows = new ArrayList<T>();
	};
	
	public PageVO(Page<T> pageData) {
		if (pageData == null) {
			this.total = 0;
			this.rows = new ArrayList<T>(0);
		} else {
			this.total = pageData.getTotalElements();
			this.rows = pageData.getContent();
		}
	}
	
	public PageVO(List<T> dataList){
		if (dataList == null) {
			this.total = 0;
			this.rows = new ArrayList<T>(0);
		} else {
			this.total = dataList.size();
			this.rows = dataList;
		}
	}
	
	public PageVO(long total, List<T> rows) {
		this.total = total;
		this.rows = rows;
	}

	/** 总记录数 */
	private long total;
	
	/** 数据集合 */
	private List<T> rows;
}
