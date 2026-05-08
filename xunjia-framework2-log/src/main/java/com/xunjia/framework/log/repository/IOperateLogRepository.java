package com.xunjia.framework.log.repository;

import com.xunjia.framework.log.entity.OperateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 操作日志JPA接口
 * 2023年1月5日
 * @author 姜浩
 */
public interface IOperateLogRepository extends JpaRepository<OperateLog, String> {

    Page<OperateLog> findAll(Specification<OperateLog> spec, Pageable pageable);
}
