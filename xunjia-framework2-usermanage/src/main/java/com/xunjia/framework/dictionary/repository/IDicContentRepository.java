package com.xunjia.framework.dictionary.repository;

import com.xunjia.framework.usermanage.entity.DicContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * 字典内容JPA持久化接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface IDicContentRepository extends JpaRepository<DicContent, String> {
	
	/**
	 * 根据编码查询字典内容
	 * @param code 内容编码
	 * @return
	 */
	public DicContent findByCode(String code);
	
	public List<DicContent> findByCodeIn(String[] codes);

	/**
	 * 查询字典内容分页数据
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<DicContent> findAll(Specification<DicContent> spec, Pageable pageable);
	
	/**
	 * 根据字典分类编码查询字典内容并排序
	 * @param code
	 * @return
	 */
	public List<DicContent> findByType_codeOrderByOrderNoAsc(String code);
	
	/**
	 * 根据上级内容编码查询字典内容并排序
	 * @param code
	 * @return
	 */
	public List<DicContent> findByParentContent_codeOrderByOrderNoAsc(String code);
	
	/**
	 * 根据拼音码查询字典内容并排序
	 * @param code
	 * @return
	 */
	public List<DicContent> findByPyCodeLikeOrderByOrderNoAsc(String code);
	
	/**
	 * 根据内容id批量删除
	 * @param ids
	 */
	@Modifying
	@Query("DELETE FROM DicContent WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
	
	/**
	 * 根据字典分类id批量删除
	 * @param typeIds
	 */
	@Modifying
	@Query("DELETE FROM DicContent WHERE type.id IN (?1)")
	public void deleteByTypes(String[] typeIds);
}
