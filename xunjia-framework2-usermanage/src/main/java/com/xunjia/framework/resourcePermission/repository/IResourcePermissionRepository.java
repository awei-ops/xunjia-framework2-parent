package com.xunjia.framework.resourcePermission.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 资源授权持久化接口
 * 2020年6月9日
 * @author 姜浩
 */
public interface IResourcePermissionRepository extends JpaRepository<ResourcePermission, String> {

	@Modifying
	@Query("DELETE FROM ResourcePermission WHERE ownerType = ?1 AND ownerId IN (?2)")
	void deleteByOwner(String ownerType, String ownerIds[]);
	
	@Modifying
	@Query("DELETE FROM ResourcePermission WHERE ownerType = ?1 AND ownerId = ?2 AND resourceId IN (?3)")
	void deleteByOwner(String ownerType, String ownerId, String resourceIds[]);
	
	@Modifying
	@Query("DELETE FROM ResourcePermission WHERE resourceId IN (?1)")
	void deleteByResource(String[] resourceIds);

	@Modifying
	@Query("DELETE FROM ResourcePermission WHERE resourceId = ?1")
	void deleteByResource(String resourceId);
	
	List<ResourcePermission> findByOwnerTypeAndOwnerIdIn(String ownerType, String[] ownerIds);
	
	List<ResourcePermission> findByOwnerTypeAndOwnerId(String ownerType, String ownerId);
}
