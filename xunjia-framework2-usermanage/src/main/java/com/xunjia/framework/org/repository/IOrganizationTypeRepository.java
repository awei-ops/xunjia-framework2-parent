package com.xunjia.framework.org.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.OrganizationType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 组织分类JPA接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface IOrganizationTypeRepository extends JpaRepository<OrganizationType, String> {

	/**
	 * 查询所有组织分类信息并排序
	 */
	public List<OrganizationType> findAll(Sort sort);
	
	/**
	 * 根据名称查询组织分类信息
	 * @param name
	 * @return
	 */
	public OrganizationType findByName(String name);
	
	public List<OrganizationType> findByNameIn(String[] names);
	
	/**
	 * 批量删除组织分类信息
	 * @param ids
	 */
	@Modifying
	@Query("DELETE FROM OrganizationType WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
}
