package com.xunjia.framework.user.service;

import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.usermanage.entity.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.user.repository.IUserRepository;
import com.xunjia.framework.user.repository.IUserSettingsRepository;

@Transactional
@Service
public class UserSettingsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsService.class);
	
	@Autowired
	private IUserSettingsRepository repo;
	
	@Autowired
	private IUserRepository userRepo;
	
	public UserSettings findByUserId(String userId) {
		return repo.findByUserId(userId);
	}
	
	public ResponseData<Boolean> saveTheme(String theme, String userId){
		ResponseData<Boolean> resp = null;
		try {
			UserSettings userSettings = repo.findByUserId(userId);
			User user = null;
			if (userSettings == null) {
				userSettings = new UserSettings();
				user = userRepo.findById(userId).get();
			}
			userSettings.setUserId(userId);
			userSettings.setCustomTheme(theme);
			repo.save(userSettings);
			Context.setCurrentUserSettings(userSettings);
			
			if (user != null) {
				user.setUserSettings(userSettings);
				userRepo.save(user);
			}
			
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserSettingsService.saveTheme方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
}
