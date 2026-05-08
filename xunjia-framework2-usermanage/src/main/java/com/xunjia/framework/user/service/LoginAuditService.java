package com.xunjia.framework.user.service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.usermanage.entity.LoginAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.user.repository.ILoginAuditRepository;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;

/**
 * 登录审计业务服务
 * 2020年5月9日
 * @author 姜浩
 */
@Service
@Transactional
public class LoginAuditService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuditService.class);

	@Autowired
	private ILoginAuditRepository repo;
	
	/**
	 * 保存登录日志
	 * @param username	用户名
	 * @param ip				客户端ip
	 * @param isSuccess	登录是否成功
	 */
	public void save(String username, String ip, String from, boolean isSuccess) {
		LoginAudit la = new LoginAudit();
		la.setIp(ip);
		la.setLoginTime(new Date());
		la.setUsername(username);
		la.setFrom(from);
		la.setResult(isSuccess ? 1 : 0);
		
		repo.save(la);
	}
	
	/**
	 * 清除登录日志
	 * @param startDate	起始日期
	 * @param endDate		截止日期
	 * @return
	 */
	public ResponseData<Boolean> clear(String startDate, String endDate) {
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByDate(DateUtils.parse(startDate, "yyyy-MM-dd"), 
					DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (ParseException e) {
			LOGGER.error("LoginAuditService.clear方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 查询登录日志分页信息
	 * @param startDate
	 * @param endDate
	 * @param loginResult
	 * @param pageIndex
	 * @param rows
	 * @return
	 */
	public Page<LoginAudit> findLoginAuditRecords(String startDate, String endDate, String from, int loginResult, int pageIndex, int rows){
		Specification<LoginAudit> spec = new Specification<LoginAudit>() {
			public Predicate toPredicate(Root<LoginAudit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				try {
					if (!StringUtils.isEmpty(startDate)) {
						Predicate predicate = cb.greaterThanOrEqualTo(root.get("loginTime").as(Date.class), DateUtils.parse(startDate, "yyyy-MM-dd"));
						predicates.add(predicate);
					}
					if (!StringUtils.isEmpty(endDate)) {
						Predicate predicate = cb.lessThanOrEqualTo(root.get("loginTime").as(Date.class), DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
						predicates.add(predicate);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (loginResult != -1) {
					Predicate predicate = cb.equal(root.get("result").as(Integer.class), loginResult);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(from)){
					Predicate predicate = cb.equal(root.get("from").as(String.class), from);
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "id");
		Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
		Page<LoginAudit> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("LoginAuditService.findLoginAuditRecords方法异常。", e);
		}
		return pageData;
	}
}
