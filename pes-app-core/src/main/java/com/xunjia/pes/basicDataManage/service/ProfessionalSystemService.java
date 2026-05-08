package com.xunjia.pes.basicDataManage.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.ProfessionalSystem;
import com.xunjia.pes.basicDataManage.repository.IProfessionalSystemRepository;
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
public class ProfessionalSystemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalSystem.class);

    @Autowired
    IProfessionalSystemRepository repository;

    public ResponseData<Boolean> save(ProfessionalSystem param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                ProfessionalSystem professionalSystem = repository.findByEvaluationIndexNameAndProfessionalTypeAndDeleteFlag(param.getEvaluationIndexName(), param.getProfessionalType(), 0);
                if (professionalSystem != null) {
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
            LOGGER.error("ProfessionalSystemService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(ProfessionalSystem param, String originalName) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                if (param.getEvaluationIndexName().equals(originalName)) {
                    repository.save(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    ProfessionalSystem sameObject = repository.findByEvaluationIndexNameAndProfessionalTypeAndDeleteFlag(param.getEvaluationIndexName(), param.getProfessionalType(), 0);
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
            LOGGER.error("ProfessionalSystemService.update方法异常。", e);
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
            LOGGER.error("ProfessionalSystemService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ProfessionalSystem findById(String id) {
        return repository.findById(id).get();
    }

    public Page<ProfessionalSystem> findProfessionalSystems(String workAreaCode, String professionalSystemCode, String evaluationIndexName, String evaluationIndexLevel, String weights, String professionalType, int page, int size) {
        Specification<ProfessionalSystem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<Predicate>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);

            if (!StringUtils.isEmpty(workAreaCode)) {
                Predicate predicate = cb.equal(root.get("workAreaCode").as(String.class), workAreaCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(professionalSystemCode)) {
                Predicate predicate = cb.equal(root.get("professionalSystemCode").as(String.class), professionalSystemCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(professionalType)) {
                Predicate predicate = cb.equal(root.get("professionalType").as(String.class), professionalType);
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
        Page<ProfessionalSystem> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("ProfessionalSystemService.findProfessionalSystems。", e);
        }
        return pageData;
    }
}
