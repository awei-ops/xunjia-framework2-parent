package com.xunjia.framework.sysSettings.auto;

import java.util.List;

import javax.annotation.PostConstruct;

import com.xunjia.framework.usermanage.entity.SysSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xunjia.framework.sysSettings.service.SysSettingService;

@Component
public class InitSysSettings {

	public static List<SysSetting> sysSettings;
	
	@Autowired
	private SysSettingService service;
	
	
	@PostConstruct
	public void initSysSettings() {
		sysSettings = service.findSysSettings();
	}
}
