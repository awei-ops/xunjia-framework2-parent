package com.xunjia.framework.user.repository;

import java.util.Date;

import com.xunjia.framework.usermanage.entity.LoginAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 登录审计JPA接口
 * 2020年5月9日
 * @author 姜浩
 */
public interface ILoginAuditRepository extends JpaRepository<LoginAudit, String> {

	/**
	 * 查询登录日志分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<LoginAudit> findAll(Specification<LoginAudit> spec, Pageable pageable);
	
	/**
	 * 删除起止时间段内的登录日志
	 * @param startDate
	 * @param endDate
	 */
	@Modifying
	@Query("DELETE FROM LoginAudit WHERE loginTime BETWEEN ?1 AND ?2")
	public void deleteByDate(Date startDate, Date endDate);
}
