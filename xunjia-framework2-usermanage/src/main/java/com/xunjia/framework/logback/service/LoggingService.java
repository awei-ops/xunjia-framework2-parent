package com.xunjia.framework.logback.service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.usermanage.entity.LoggingEvent;
import com.xunjia.framework.usermanage.entity.LoggingEventException;
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

import com.xunjia.framework.logback.repository.ILoggingEventExceptionRepository;
import com.xunjia.framework.logback.repository.ILoggingEventRepository;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;

/**
 * 系统日志服务
 * 2020年5月8日
 * @author 姜浩
 */
@Service
public class LoggingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingService.class);
	
	@Autowired
	private ILoggingEventRepository logRepo;
	
	@Autowired
	private ILoggingEventExceptionRepository exRepo;
	
	/**
	 * 根据事件id查询异常信息
	 * @param eventId
	 * @return
	 */
	public List<LoggingEventException> findExceptionByEventId(long eventId){
		return exRepo.findByEventIdOrderByIAsc(eventId);
	}
	
	/**
	 *  查询系统事件分页信息
	 * @param startDate
	 * @param endDate
	 * @param level
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<LoggingEvent> findLoggingEvent(String startDate, String endDate, String level, int page, int rows){
		Specification<LoggingEvent> spec = new Specification<LoggingEvent>() {
			public Predicate toPredicate(Root<LoggingEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(startDate)) {
					Date date = null;
					try {
						date = DateUtils.parse(startDate, "yyyy-MM-dd");
					} catch (ParseException e) {
						LOGGER.error("LoggingService.findLoggingEvent方法异常。", e);
					}
					long timestmp = date.getTime();
					Predicate predicate = cb.greaterThanOrEqualTo(root.get("timestmp").as(Long.class), timestmp);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(endDate)) {
					Date date = null;
					try {
						date = DateUtils.parse(endDate, "yyyy-MM-dd");
					} catch (ParseException e) {
						LOGGER.error("LoggingService.findLoggingEvent方法异常。", e);
					}
					long timestmp = date.getTime();
					Predicate predicate = cb.lessThanOrEqualTo(root.get("timestmp").as(Long.class), timestmp);
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "timestmp");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<LoggingEvent> pageData = null;
		try {
			pageData = logRepo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("LoggingService.findLoggingEvent方法异常。", e);
		}
		return pageData;
	}
}
