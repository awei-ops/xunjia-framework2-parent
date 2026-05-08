package com.xunjia.framework.org.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * 组织机构JPA接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface IOrganizationRepository extends JpaRepository<Organization, String> {

	/**
	 * 根据上级组织id查询组织信息并排序
	 * @param parentId
	 * @return
	 */
	List<Organization> findByParent_idAndDeleteFlagOrderByOrderNoAsc(String parentId, int deleteFlag);
	
	/**
	 * 查询上级id为null的组织信息并排序
	 * @return
	 */
	List<Organization> findByParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(int deleteFlag);
	
	/**
	 * 根据上级组织id查询可用的组织信息并排序
	 * @param enable
	 * @param parentId
	 * @return
	 */
	List<Organization> findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(int enable, String parentId, int deleteFlag);
	
	/**
	 * 查询上级组织id为null且状态为可用的组织信息并排序
	 * @param enable
	 * @return
	 */
	List<Organization> findByEnableAndParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(int enable, int deleteFlag);
	
	/**
	 * 根据组织分类id查询组织信息并排序
	 * @param typeId
	 * @return
	 */
	List<Organization> findByType_idAndParent_idAndDeleteFlagOrderByOrderNoAsc(String typeId, String parentId, int deleteFlag);
	
	/**
	 * 根据多个组织分类id查询组织信息并排序
	 * @param typeIds
	 * @return
	 */
	List<Organization> findByType_idInAndParent_idAndDeleteFlagOrderByOrderNoAsc(String[] typeIds, String parentId, int deleteFlag);
	
	/**
	 * 根据组织层级查询组织信息并排序
	 * @param level
	 * @return
	 */
	List<Organization> findByLevelAndDeleteFlagOrderByOrderNoAsc(int level, int deleteFlag);
	
	/**
	 * 根据组织名称和上级组织id查询组织信息并排序
	 * @param name
	 * @param parentId
	 * @return
	 */
	Organization findByNameAndParent_idAndDeleteFlagOrderByOrderNoAsc(String name, String parentId, int deleteFlag);
	
	/**
	 * 查询组织机构分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<Organization> findAll(Specification<Organization> spec, Pageable pageable);
	
	/**
	 * 批量删除组织信息
	 * @param ids
	 */
	@Modifying
	@Query("UPDATE Organization SET deleteFlag = 1 WHERE id IN (?1)")
	void deleteByIds(String[] ids);
	
	/**
	 * 根据上级组织id删除组织信息
	 * @param parentId
	 */
	@Modifying
	@Query("UPDATE Organization SET deleteFlag = 1 WHERE parent.id = ?1")
	void deleteByParentId(String parentId);
	
	/**
	 * 批量更新组织机构可用状态
	 * @param enable
	 * @param ids
	 */
	@Modifying
	@Query("UPDATE Organization SET enable = ?1 WHERE id IN (?2)")
	void updateEnableState(int enable, String[] ids);
	
	/**
	 * 根据给定的组织id查询组织信息并排序
	 * @param ids
	 * @return
	 */
	List<Organization> findByIdInAndDeleteFlagOrderByOrderNoAsc(String[] ids, int deleteFlag);
	
	long countByType_idInAndDeleteFlag(String[] typeIds, int deleteFlag);
	
	Organization findByCodeAndDeleteFlag(String code, int deleteFlag);
	
	List<Organization> findByCodeInAndDeleteFlag(String[] codes, int deleteFlag);
	
	List<Organization> findByCodeInAndDeleteFlag(List<String> codes, int deleteFlag);

	@Query("SELECT MAX(orderNo) FROM Organization WHERE deleteFlag = 0 AND parent.id = ?1")
	Integer findMaxOrderNo(String orgId);

	List<Organization> findByNameInAndDeleteFlag(String[] orgNames, int deleteFlag);
}
