package com.xunjia.framework.common.response;

import lombok.Data;

/**
 * 后端返回信息
 * @author 姜浩
 * @param <T> 返回的对象类型
 */
@Data
public class ResponseData<T extends Object> {
	
	/** HTTP状态码 */
	protected int httpCode;
	
	/** 操作结果 */
	protected boolean result;
	
	/** 提示信息 */
	protected String msg;
	
	/** 附加信息 */
	protected T data;

	protected ResponseData(){}
	
	protected ResponseData(int httpCode, boolean result, String msg, T data) {
		this.httpCode = httpCode;
		this.result = result;
		this.msg = msg;
		this.data = data;
	}
	
	public static<T> ResponseData<T> getSuccess(String msg) {
		return new ResponseData<T>(HttpCode.OK, true, msg, null);
	}
	
	public static<T> ResponseData<T> getSuccess(String msg, T data) {
		return new ResponseData<T>(HttpCode.OK, true, msg, data);
	}
	
	public static<T> ResponseData<T> getFail( String msg) {
		return new ResponseData<T>(HttpCode.OK, false, msg, null);
	}
	
	public static<T> ResponseData<T> getFail(String msg, T data) {
		return new ResponseData<T>(HttpCode.OK, false, msg, data);
	}
	
	public static<T> ResponseData<T> getFail(int httpCode, String msg, T data) {
		return new ResponseData<T>(httpCode, false, msg, data);
	}
	
	public static<T> ResponseData<T> getError(Exception e){
		return new ResponseData<T>(HttpCode.INTERNAL_SERVER_ERROR, false, 
				ResponseMsg.COMMON_FAIL + e.getMessage(), null);
	}
}
