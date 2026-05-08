package com.xunjia.framework.sysSettings.controller;

import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.SysSetting;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.sysSettings.service.SysSettingService;
import com.xunjia.framework.utils.ArrayUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;

@Api(value="系统配置控制器")
@RestController
@RequestMapping("/sysSetting")
public class SysSettingsController {

	@Autowired
	private SysSettingService service;
	
	@RequestMapping("/toList")
	@RequiresPermissions("sysSetting:list")
	public ModelAndView toList() {
		ModelAndView mav = new ModelAndView("framework/sysSetting/list");
		return mav;
	}
	
	@RequestMapping("/findSysSettings")
	@RequiresPermissions("sysSetting:list")
	public PageVO<SysSetting> findSysSettings(){
		List<SysSetting> settings = service.findSysSettings();
		PageVO<SysSetting> pageVo = new PageVO<SysSetting>(settings.size(), settings);
		return pageVo;
	}
	
	@RequestMapping("/findAllSysSettings")
	public List<SysSetting> findAllSysSettings(){
		return service.findSysSettings();
	}
	
	@RequestMapping("/save")
	@RequiresPermissions("sysSetting:save")
	public ResponseData<Boolean> save(@RequestParam(name="keys[]")String[] keys,
			@RequestParam(name="values[]") String[] values){
		ResponseData<Boolean> resp = null;
		if (!ArrayUtils.isArrayEmpty(keys) && !ArrayUtils.isArrayEmpty(values)) {
			List<SysSetting> settings = new ArrayList<SysSetting>();
			for (int i = 0; i < keys.length; i++) {
				if (!StringUtils.isEmpty(keys[i])) {
					SysSetting setting = new SysSetting();
					setting.setKey(keys[i]);
					setting.setValue(values[i]);
					settings.add(setting);
				}
			}
			resp = service.saveBatch(settings);
		} else {
			resp = ResponseData.getFail(ResponseMsg.SETTING_WRONG);
		}
		return resp;
	}
	
	@RequestMapping("/delete")
	@RequiresPermissions("sysSetting:delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]")String[] ids){
		ResponseData<Boolean> resp = service.deleteByIds(ids);
		return resp;
	}
}
