package com.xunjia.framework.orgPermission.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.OrgPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 有权组织记录JPA接口
 * 2020年5月9日
 * @author 姜浩
 */
public interface IOrgPermissionRepository extends JpaRepository<OrgPermission, String> {

	public Page<OrgPermission> findAll(Specification<OrgPermission> spec, Pageable pageable);
	
	/**
	 * 根据权限所有者类型和所有者id查询下级有权组织信息
	 * @param ownerType
	 * @param ownerIds
	 * @param parentOrgId
	 * @return
	 */
	public List<OrgPermission> findByOwnerTypeAndOwnerIdInAndOrg_parent_id(String ownerType, String[] ownerIds, String parentOrgId);
	
	public List<OrgPermission> findByOwnerTypeAndOwnerIdInAndOrg_parentIsNull(String ownerType, String[] ownerIds);
	
	/**
	 * 根据权限所有者类型和所有者id查询下级有权组织信息
	 * @param ownerType
	 * @param ownerId
	 * @param parentOrgId
	 * @return
	 */
	public List<OrgPermission> findByOwnerTypeAndOwnerIdAndOrg_parent_id(String ownerType, String ownerId, String parentOrgId);
	
	public List<OrgPermission> findByOwnerTypeAndOwnerIdAndOrg_parentIsNull(String ownerType, String ownerId);
	
	/**
	 * 根据权限所有者类型、所有者id和菜单id查询有权组织信息
	 * @param ownerType
	 * @param ownerId
	 * @param menuIds
	 * @return
	 */
	public List<OrgPermission> findByOwnerTypeAndOwnerIdAndOrg_idIn(String ownerType, String ownerId, String[] orgIds);
	
	public List<OrgPermission> findByOwnerTypeAndOwnerId(String ownerType, String ownerId);
	
	public List<OrgPermission> findByOwnerTypeAndOwnerIdIn(String ownerType, String[] ownerIds);
	
	/**
	 * 删除指定权限所有者类型和id的有权组织记录
	 * @param ownerType
	 * @param ownerIds
	 */
	@Modifying
	@Query("DELETE FROM OrgPermission WHERE ownerType = ?1 AND ownerId IN (?2)")
	public void deleteByOwner(String ownerType, String[] ownerIds);
	
	/**
	 * 根据所有者类型、所有者id和菜单id批量删除权限记录
	 * @param ownerType
	 * @param ownerId
	 * @param menuIds
	 */
	@Modifying
	@Query("DELETE FROM OrgPermission WHERE ownerType = ?1 AND ownerId = ?2 AND org.id IN (?3)")
	public void deleteByOwner(String ownerType, String ownerId, String[] orgIds);
	
	@Modifying
	@Query("DELETE FROM OrgPermission WHERE org.id IN (?1)")
	public void deleteByOrgIds(String[] orgIds);
}
