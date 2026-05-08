package com.xunjia.framework.logback.repository;

import com.xunjia.framework.usermanage.entity.LoggingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 系统事件记录JPA接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface ILoggingEventRepository extends JpaRepository<LoggingEvent, Long> {

	/**
	 * 查询系统事件分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<LoggingEvent> findAll(Specification<LoggingEvent> spec, Pageable pageable);
}
