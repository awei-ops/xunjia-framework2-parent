package com.xunjia.framework.news.repository;

import com.xunjia.framework.news.entity.NewsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface INewsContentRepository extends JpaRepository<NewsContent, String> {

	NewsContent findByNews_id(String newsId);
	
	@Modifying
	@Query("DELETE FROM NewsContent WHERE news.id IN (?1)")
	void deleteByNewsIds(String[] newsIds);
}
