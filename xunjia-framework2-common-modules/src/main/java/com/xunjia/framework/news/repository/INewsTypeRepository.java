package com.xunjia.framework.news.repository;

import java.util.List;

import com.xunjia.framework.news.entity.NewsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface INewsTypeRepository extends JpaRepository<NewsType, String> {

	List<NewsType> findByParent_id(String parentId);
	
	List<NewsType> findByParentIsNull();
	
	Page<NewsType> findAll(Specification<NewsType> spec, Pageable pageable);
	
	NewsType findByName(String name);
	
	@Modifying
	@Query("DELETE FROM NewsType WHERE id IN (?1)")
	void deleteByIds(String[] ids);
}
