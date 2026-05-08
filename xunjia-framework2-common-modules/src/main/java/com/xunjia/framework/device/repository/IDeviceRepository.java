package com.xunjia.framework.device.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xunjia.framework.device.entity.Device;

public interface IDeviceRepository extends JpaRepository<Device,String>{
	
	/**
	 * 查询装置信息
	 * 2020年8月31日
	 * @author 姜浩
	 * @param spec 查询条件
	 * @param pageable 分页条件
	 * @return 装置分页数据
	 */
	public Page<Device> findAll(Specification<Device> spec, Pageable pageable);
	
	/**
	 * 根据给定组织id查询下属装置信息
	 * 2020年8月31日
	 * @author 姜浩
	 * @param orgId 组织id
	 * @return 装置信息集合
	 */
	public List<Device> findByOrg_idOrderByOrderNoAsc(String orgId);
	
	/**
	 * 更新装置可用状态
	 * 2020年8月31日
	 * @author 姜浩
	 * @param enabled 可用状态
	 * @param ids 装置id
	 */
	@Modifying
	@Query("UPDATE Device SET enabled  = ?1 WHERE id IN (?2)")
	public void updateEnable(int enabled, String[] ids);
	
	@Modifying
	@Query("DELETE FROM Device WHERE id IN (?1)")
	public void delete(String[] ids);
}
