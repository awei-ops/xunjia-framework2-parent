package com.xunjia.framework.sysSettings.service;

import java.util.List;
import java.util.Optional;

import com.xunjia.framework.usermanage.entity.SysSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.sysSettings.auto.InitSysSettings;
import com.xunjia.framework.sysSettings.repository.ISysSettingRepository;

@Transactional
@Service
public class SysSettingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SysSettingService.class);
	
	@Autowired
	private ISysSettingRepository repo;
	
	public ResponseData<Boolean> saveBatch(List<SysSetting> settings) {
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteAll();
			repo.saveAll(settings);
			
			InitSysSettings.sysSettings.clear();
			InitSysSettings.sysSettings.addAll(settings);
			
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("SysSettingService.saveBatch方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> deleteByIds(String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByIds(ids);
			for (String id : ids) {
				Optional<SysSetting> settingOptional = InitSysSettings.sysSettings
						.stream().filter(c -> c.getId().equals(id)).findFirst();
				if (settingOptional.isPresent()) {
					InitSysSettings.sysSettings.remove(settingOptional.get());
				}
			}
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("SysSettingService.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public List<SysSetting> findSysSettings(){
		return repo.findAll();
	}
}
