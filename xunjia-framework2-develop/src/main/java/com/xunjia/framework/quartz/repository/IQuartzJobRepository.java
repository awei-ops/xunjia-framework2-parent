package com.xunjia.framework.quartz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xunjia.framework.quartz.entity.QuartzJob;

import java.util.List;

public interface IQuartzJobRepository extends JpaRepository<QuartzJob, String> {

	Page<QuartzJob> findAll(Specification<QuartzJob> spec, Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM QuartzJob WHERE id IN (?1)")
	void deleteByIds(String[] ids);

	List<QuartzJob> findByStarted(int started);
}
