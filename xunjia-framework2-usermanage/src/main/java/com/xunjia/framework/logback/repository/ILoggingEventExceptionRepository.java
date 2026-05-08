package com.xunjia.framework.logback.repository;

import java.util.List;

import com.xunjia.framework.usermanage.entity.LoggingEventException;
import com.xunjia.framework.usermanage.entity.LoggingEventExceptionKey;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 系统事件异常记录JPA接口
 * 2020年5月8日
 * @author 姜浩
 */
public interface ILoggingEventExceptionRepository extends JpaRepository<LoggingEventException, LoggingEventExceptionKey> {

	/**
	 * 根据事件id查询异常信息并排序
	 * @param eventId
	 * @return
	 */
	public List<LoggingEventException> findByEventIdOrderByIAsc(long eventId);
}
