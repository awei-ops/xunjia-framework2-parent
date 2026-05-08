package com.xunjia.framework.news.repository;

import com.xunjia.framework.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface INewsRepository extends JpaRepository<News, String> {

	Page<News> findAll(Specification<News> spec, Pageable pageable);
	
	int countByType_id(String typeId);
	
	@Modifying
	@Query("UPDATE News SET readCount = readCount + 1 WHERE id = ?1")
	void updateReadCount(String id);
	
	@Modifying
	@Query("UPDATE News SET auditState = ?1 WHERE id IN (?2)")
	void updateAuditState(int auditState, String[] ids);
	
	@Modifying
	@Query("DELETE FROM News WHERE id IN (?1)")
	void deleteByIds(String[] ids);
	
	News findFirstByAuditStateAndType_nameOrderByPublishDateDesc(int auditState, String typeName);
}
