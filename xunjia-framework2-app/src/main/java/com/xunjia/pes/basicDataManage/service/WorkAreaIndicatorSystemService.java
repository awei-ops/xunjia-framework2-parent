package com.xunjia.pes.basicDataManage.service;

import com.xunjia.pes.basicDataManage.entity.WorkAreaIndicatorSystem;
import com.xunjia.pes.basicDataManage.repository.IWorkAreaIndicatorSystemRepository;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class WorkAreaIndicatorSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkAreaIndicatorSystemService.class);

    @Autowired
    IWorkAreaIndicatorSystemRepository repository;

    public ResponseData<Boolean> save(WorkAreaIndicatorSystem param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                WorkAreaIndicatorSystem workAreaIndicatorSystem = repository.findByEvaluationIndexNameAndDeleteFlag(param.getEvaluationIndexName(), 0);
                if (workAreaIndicatorSystem != null) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
                    return resp;
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_NULL);
                return resp;
            }
            repository.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("WorkAreaIndicatorSystemService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(WorkAreaIndicatorSystem param, String originalName) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                if (param.getEvaluationIndexName().equals(originalName)) {
                    repository.save(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    WorkAreaIndicatorSystem sameObject = repository.findByEvaluationIndexNameAndDeleteFlag(param.getEvaluationIndexName(), 0);
                    if (sameObject == null) {
                        repository.save(param);
                        resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                    } else {
                        resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
                    }
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_NULL);
            }
        } catch (Exception e) {
            LOGGER.error("WorkAreaIndicatorSystemService.update方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> deleteByIds(String[] ids) {
        ResponseData<Boolean> resp;
        try {
            repository.deleteByIds(ids);
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("WorkAreaIndicatorSystemService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public WorkAreaIndicatorSystem findById(String id) {
        return repository.findById(id).get();
    }

    public Page<WorkAreaIndicatorSystem> findWorkAreaIndicatorSystems(String workAreaCode, String workAreaName, String evaluationIndexName, String evaluationIndexLevel, String weights, int page, int size) {
        Specification<WorkAreaIndicatorSystem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<Predicate>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);

            if (!StringUtils.isEmpty(workAreaCode)) {
                Predicate predicate = cb.equal(root.get("workAreaCode").as(String.class), workAreaCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(workAreaName)) {
                Predicate predicate = cb.equal(root.get("workAreaName").as(String.class), workAreaName);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(evaluationIndexName)) {
                Predicate predicate = cb.like(root.get("evaluationIndexName").as(String.class), "%" + evaluationIndexName + "%");
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(evaluationIndexLevel)) {
                Predicate predicate = cb.equal(root.get("evaluationIndexLevel").as(String.class), evaluationIndexLevel);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(weights)) {
                Predicate predicate = cb.equal(root.get("weights").as(String.class), weights);
                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "workAreaCode");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<WorkAreaIndicatorSystem> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("WorkAreaIndicatorSystemService.findWorkAreaIndicatorSystems方法异常。", e);
        }
        return pageData;
    }
}
