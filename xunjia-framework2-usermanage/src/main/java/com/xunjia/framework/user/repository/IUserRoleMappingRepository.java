package com.xunjia.framework.user.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.UserRoleMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 用户角色对照JPA接口
 * 2020年5月9日
 * @author 姜浩
 */
public interface IUserRoleMappingRepository extends JpaRepository<UserRoleMapping, String> {

	/**
	 * 删除指定用户的对照关系
	 * @param userIds
	 */
	@Modifying
	@Query("DELETE FROM UserRoleMapping WHERE user.id IN (?1)")
	public void deleteByUserIds(String[] userIds);
	
	/**
	 * 删除指定角色的对照关系
	 * @param roleIds
	 */
	@Modifying
	@Query("DELETE FROM UserRoleMapping WHERE role.id IN (?1)")
	public void deleteByRoleIds(String[] roleIds);
	
	@Modifying
	@Query("DELETE FROM UserRoleMapping WHERE role.id = ?1 AND user.id IN (?2)")
	public void deleteEntity(String roleId, String[] userIds);
	
	@Modifying
	@Query("DELETE FROM UserRoleMapping WHERE role.id IN (?1) AND user.id = ?2")
	public void deleteEntity(String[] roleIds, String userId);
	
	/**
	 * 根据角色id查询对照关系
	 * @param roleId
	 * @return
	 */
	public List<UserRoleMapping> findByRole_id(String roleId);
	
	/**
	 * 根据用户id查询对照关系
	 * @param userId
	 * @return
	 */
	public List<UserRoleMapping> findByUser_id(String userId);
	
	/**
	 * 查询对照关系分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<UserRoleMapping> findAll(Specification<UserRoleMapping> spec, Pageable pageable);

}
