package com.xunjia.framework.log.repository;

import com.xunjia.framework.log.entity.QuartzJobLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务调度日志JPA接口
 * 2023年4月14日
 * @author 姜浩
 */
public interface IQuartzJobLogRepository extends JpaRepository<QuartzJobLog, String> {

    Page<QuartzJobLog> findAll(Specification<QuartzJobLog> spec, Pageable pageable);
}
