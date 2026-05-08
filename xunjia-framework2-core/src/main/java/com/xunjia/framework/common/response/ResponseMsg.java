package com.xunjia.framework.common.response;

/**
 * 通用的响应消息
 * 2020年8月4日
 * @author 姜浩
 */
public class ResponseMsg {

	public static final String COMMON_SUCCESS = "操作成功。";
	
	public static final String COMMON_FAIL = "操作失败。";
	
	public static final String COMMON_FAIL_CODE_EXIST = "操作失败，系统中已存在相同编码。";
	
	public static final String COMMON_FAIL_NAME_EXIST = "操作失败，系统中已存在相同名称。";

	public static final String COMMON_FAIL_NAME_NULL = "操作失败，名称为空。";
	
	public static final String COMMON_FAIL_EQUIP_EXIST = "操作失败，系统已绑定过该设备。";
	
	public static final String LOGIN_FAIL = "登录失败。";
	
	public static final String SAVE_SUCCESS = "保存成功。";
	
	public static final String SAVE_FAIL = "保存失败。";
	
	public static final String UPDATE_SUCCESS = "更新成功。";
	
	public static final String UPDATE_FAIL = "更新失败。";
	
	public static final String DELETE_SUCCESS = "删除成功。";
 	
	public static final String DELETE_FAIL = "删除失败。";
	
	public static final String DELETE_FAIL_SUB_EXIST = "删除失败，存在下级数据。";
	
	public static final String DELETE_FAIL_SUB_ORG_EXIST = "删除失败，存在下级组织机构。";
	
	public static final String DELETE_FAIL_SUB_USER_EXIST = "删除失败，存在下级用户信息。";
	
	public static final String DELETE_FAIL_ADMIN = "不能删除系统管理员账户。";
	
	public static final String SUBMIT_SUCCESS = "提交成功。";
	
	public static final String SUBMIT_FAIL = "提交失败。";
	
	public static final String UPLOAD_SUCCESS = "上传成功。";
	
	public static final String UPLOAD_FAIL = "上传失败。";
	
	public static final String SETTING_WRONG = "未传入有效的配置信息。";
	
	public static final String OLD_PASSWORD_WRONG = "原密码输入有误。";
	
	public static final String IMPORT_SUCCESS = "导入成功。";
	
	public static final String IMPORT_FAIL = "导入失败。";
	
	public static final String EXPORT_SUCCESS = "导出成功。";
	
	public static final String EXPORT_FAIL = "导出失败。";
	
	public static final String CLASS_NOT_FOUND = "未找到指定的类。";

	public static final String RESET_SUCCESS = "重置成功。";

	public static final String CONFIRM_SUCCESS = "确认成功。";

	public static final String RETRUN_SUCCESS = "回收确认成功。";

	public static final String CONFIRM_FAIL = "确认失败。";

	public static final String COMMON_FAIL_CODE_NULL ="操作失败，编码为空。";
}
