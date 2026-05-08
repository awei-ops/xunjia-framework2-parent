package com.xunjia.framework.sysSettings.repository;

import com.xunjia.framework.usermanage.entity.SysSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ISysSettingRepository extends JpaRepository<SysSetting, String> {

	@Modifying
	@Query("DELETE FROM SysSetting WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
}
