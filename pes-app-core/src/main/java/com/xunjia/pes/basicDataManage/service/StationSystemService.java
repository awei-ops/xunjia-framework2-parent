package com.xunjia.pes.basicDataManage.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.StationSystem;
import com.xunjia.pes.basicDataManage.repository.IStationSystemRepository;
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
public class StationSystemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationSystem.class);

    @Autowired
    IStationSystemRepository repository;

    public ResponseData<Boolean> save(StationSystem param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                StationSystem StationSystem = repository.findByEvaluationIndexNameAndStationTypeAndDeleteFlag(param.getEvaluationIndexName(), param.getStationType(), 0);
                if (StationSystem != null) {
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
            LOGGER.error("StationSystemService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(StationSystem param, String originalName) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                if (param.getEvaluationIndexName().equals(originalName)) {
                    repository.save(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    StationSystem sameObject = repository.findByEvaluationIndexNameAndStationTypeAndDeleteFlag(param.getEvaluationIndexName(), param.getStationType(), 0);
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
            LOGGER.error("StationSystemService.update方法异常。", e);
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
            LOGGER.error("StationSystemService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public StationSystem findById(String id) {
        return repository.findById(id).get();
    }

    public Page<StationSystem> findStationSystems(String professionalSystemCode, String stationSystemCode, String evaluationIndexName, String evaluationIndexLevel, String weights, String stationType, int page, int size) {
        Specification<StationSystem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<Predicate>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);

            if (!StringUtils.isEmpty(professionalSystemCode)) {
                Predicate predicate = cb.equal(root.get("professionalSystemCode").as(String.class), professionalSystemCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(stationSystemCode)) {
                Predicate predicate = cb.equal(root.get("stationSystemCode").as(String.class), stationSystemCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(stationType)) {
                Predicate predicate = cb.equal(root.get("stationType").as(String.class), stationType);
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
        Sort sort = Sort.by(Sort.Direction.ASC, "professionalSystemCode");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<StationSystem> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("StationSystemService.findStationSystems。", e);
        }
        return pageData;
    }
}
