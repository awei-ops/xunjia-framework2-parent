package com.xunjia.framework.dictionary.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.DicType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * 字典分类JPA持久化接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface IDicTypeRepository extends JpaRepository<DicType, String> {

	/**
	 * 查询字典分类分页数据
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<DicType> findAll(Specification<DicType> spec, Pageable pageable);
	
	/**
	 * 根据编码查询字典分类信息
	 * @param code
	 * @return
	 */
	public DicType findByCode(String code);
	
	public List<DicType> findByCodeIn(String[] codes);
	
	/**
	 * 根据拼音码查询字典分类信息并排序
	 * @param code
	 * @return
	 */
	public List<DicType> findByPyCodeLikeOrderByOrderNoAsc(String code);
	
	/**
	 * 根据内容id批量删除
	 * @param ids
	 */
	@Modifying
	@Query("DELETE FROM DicType WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
}
