package com.xunjia.framework.resource.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 系统资源持久化接口
 * 2020年6月9日
 * @author 姜浩
 */
public interface IResourceRepository extends JpaRepository<Resource, String> {

	@Modifying
	@Query("DELETE FROM Resource WHERE id IN (?1)")
	void deleteByIds(String[] ids);
	
	@Modifying
	@Query("UPDATE Resource SET enable = ?1 WHERE id IN (?2)")
	void updateState(int state, String[] ids);
	
	@Modifying
	@Query("UPDATE Resource SET imgIcon = ?1 WHERE id = ?2")
	void updateImgIcon(String imgIcon, String id);
	
	Page<Resource> findAll(Specification<Resource> spec, Pageable pageable);
	
	List<Resource> findByEnableOrderByOrderNoAsc(int enable);
	
	List<Resource> findByEnableAndAllowGrantOrderByOrderNoAsc(int enable, int allowGrant);
	
	List<Resource> findByIdInOrderByOrderNoAsc(String[] ids);

	List<Resource> findByTypeAndEnableOrderByOrderNoAsc(String type, int enable);
	
	List<Resource> findByPermissionCodeAndCategory(String permissionCode, String category);

	List<Resource> findByParent_id(String parentId);

	List<Resource> findByIdIn(String[] ids);

	List<Resource> findByCategory(String category);
	
	Resource findByCode(String code);
	
	Resource findByUrl(String url);
}
