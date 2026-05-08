package com.xunjia.framework.user.repository;

import java.util.Date;
import java.util.List;

import com.xunjia.framework.usermanage.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 用户信息JPA接口
 * 2020年5月9日
 * @author 姜浩
 */
public interface IUserRepository extends JpaRepository<User, String> {

	/**
	 * 根据用户名查询用户信息
	 * @param username
	 * @return
	 */
	User findByUsername(String username);
	
	/**
	 * 根据员工编号查询用户信息
	 * @param staffCode
	 * @return
	 */
	User findByStaffCode(String staffCode);
	
	/**
	 * 根据电子设备编号查询用户信息
	 * @param eleEquipCode
	 * @return
	 */
	User findByEleEquipCodeAndDeleteFlag(String eleEquipCode, int deleteFlag);
	
	/**
	 * 根据用户名和密码查询用户信息
	 * @param username
	 * @param password
	 * @return
	 */
	User findByUsernameAndPassword(String username, String password);
	
	/**
	 * 根据组织id查询该组织下的所有用户信息
	 * @param orgId
	 * @return
	 */
	List<User> findByOrg_idAndDeleteFlag(String orgId, int deleteFlag);
	
	/**
	 * 根据id批量删除用户信息
	 * @param ids
	 */
	@Modifying
	@Query("UPDATE User SET deleteFlag = 1 WHERE id IN (?1)")
	void deleteByIds(String[] ids);
	
	/**
	 * 批量更新用户可用状态
	 * @param enable
	 * @param ids
	 */
	@Modifying
	@Query("UPDATE User SET enable = ?1 WHERE id IN (?2)")
	void updateEnableState(int enable, String[] ids);
	
	/**
	 * 更新用户密码
	 * @param password		新密码
	 * @param updateDate	密码更新日期
	 * @param userId		用户id
	 */
	@Modifying
	@Query("UPDATE User SET password = ?1, passwordExpireDate = ?2 WHERE id = ?3")
	void updatePassword(String password, Date updateDate, String userId);

	/**
	 * 查询用户分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<User> findAll(Specification<User> spec, Pageable pageable);
	
	List<User> findByIdInAndDeleteFlag(String[] ids, int deleteFlag);
	
	List<User> findByUsernameIn(List<String> usernames);
	
	List<User> findByStaffCodeIn(List<String> staffCodes);

	@Modifying
	@Query("UPDATE User SET initedFlag = ?1 WHERE id = ?2")
	void updateInitedFlag(int initedFlag, String id);

	@Query("SELECT MAX(orderNo) FROM User WHERE deleteFlag = 0 AND org.id = ?1")
	Integer findOrgMaxOrderNo(String orgId);
}
