package com.xunjia.framework.log.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.log.entity.OperateLog;
import com.xunjia.framework.log.repository.IOperateLogRepository;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 操作日志业务服务
 * 2023年1月5日
 * @author 姜浩
 */
@Service
@Transactional
@Slf4j
public class OperateLogService {

    @Autowired
    private IOperateLogRepository repository;

    public ResponseData<Boolean> save(OperateLog operateLog){
        ResponseData<Boolean> resp;
        try {
            repository.save(operateLog);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public OperateLog findById(String id){
        return repository.findById(id).get();
    }

    public Page<OperateLog> findOperateLogs(String startDate, String endDate, String module, String username, int pageIndex, int rows){
        Specification<OperateLog> spec = new Specification<OperateLog>() {
            public Predicate toPredicate(Root<OperateLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new LinkedList<>();
                try {
                    if (!StringUtils.isEmpty(startDate)) {
                        Predicate predicate = cb.greaterThanOrEqualTo(root.get("operateTime").as(Date.class), DateUtils.parse(startDate, "yyyy-MM-dd"));
                        predicates.add(predicate);
                    }
                    if (!StringUtils.isEmpty(endDate)) {
                        Predicate predicate = cb.lessThanOrEqualTo(root.get("operateTime").as(Date.class), DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
                        predicates.add(predicate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!StringUtils.isEmpty(module)) {
                    Predicate predicate = cb.like(root.get("module").as(String.class), "%" + module + "%");
                    predicates.add(predicate);
                }
                if (!StringUtils.isEmpty(username)){
                    Predicate predicate = cb.like(root.get("username").as(String.class), "%" + username + "%");
                    predicates.add(predicate);
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
        Page<OperateLog> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("OperateLogService.findOperateLogs。", e);
            pageData = new PageImpl<OperateLog>(new ArrayList<>(0));
        }
        return pageData;
    }
}
