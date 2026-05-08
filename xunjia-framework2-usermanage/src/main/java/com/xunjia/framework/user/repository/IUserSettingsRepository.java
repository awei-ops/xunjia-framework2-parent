package com.xunjia.framework.user.repository;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface IUserSettingsRepository extends JpaRepository<UserSettings, String> {
	
	public UserSettings findByUserId(String userId);
	
	@Modifying
	@Query("UPDATE UserSettings SET customTheme = ?1 WHERE userId = ?2")
	public void updateCustomTheme(String theme, String userId);
	
	@Modifying
	@Query("UPDATE UserSettings SET defaultMenu = ?1 WHERE userId = ?2")
	public void updateDefaultMenu(Resource defaultMenu, String userId);
}
