package com.xunjia.framework.role.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * 角色信息JPA接口
 * 2020年5月9日
 * @author 姜浩
 */
public interface IRoleRepository extends JpaRepository<Role, String> {

	/**
	 * 根据名称查询角色信息
	 * @param name
	 * @return
	 */
	public Role findByName(String name);
	
	/**
	 * 根据可用状态查询某组织下级角色
	 * @param enable
	 * @param orgId
	 * @return
	 */
	public List<Role> findByEnableAndOrganization_id(int enable, String orgId);

	/**
	 * 根据可用状态查询全局角色
	 * @param enable
	 * @return
	 */
	public List<Role> findByEnableAndOrganizationIsNull(int enable);
	
	/**
	 * 查询角色分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<Role> findAll(Specification<Role> spec, Pageable pageable);
	
	/**
	 * 根据id批量删除角色信息
	 * @param ids
	 */
	@Modifying
	@Query("DELETE FROM Role WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
	
	/**
	 * 批量修改角色可用状态
	 * @param enable
	 * @param ids
	 */
	@Modifying
	@Query("UPDATE Role SET enable = ?1 WHERE id IN (?2)")
	public void updateEnableState(int enable, String[] ids);

	public List<Role> findByNameIn(String[] names);

	public List<Role> findByOrganization_id(String orgId);

	public List<Role> findByOrganizationIsNull();
}
