package com.xunjia.pes.basicDataManage.service;

import com.xunjia.pes.basicDataManage.entity.DeviceBasicData;
import com.xunjia.pes.basicDataManage.repository.IDeviceBasicDataRepository;
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
public class DeviceBasicDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceBasicData.class);

    @Autowired
    IDeviceBasicDataRepository repository;

    public ResponseData<Boolean> save(DeviceBasicData param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                DeviceBasicData DeviceBasicData = repository.findByEvaluationIndexNameAndDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(param.getEvaluationIndexName(), param.getDeviceTypeCode(),param.getDeviceCategory(), 0);
                if (DeviceBasicData != null) {
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
            LOGGER.error("DeviceBasicDataService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(DeviceBasicData param, String originalName) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getEvaluationIndexName())) {
                if (param.getEvaluationIndexName().equals(originalName)) {
                    repository.save(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    DeviceBasicData sameObject = repository.findByEvaluationIndexNameAndDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(param.getEvaluationIndexName(), param.getDeviceTypeCode(),param.getDeviceCategory(), 0);
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
            LOGGER.error("DeviceBasicDataService.update方法异常。", e);
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
            LOGGER.error("DeviceBasicDataService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public DeviceBasicData findById(String id) {
        return repository.findById(id).get();
    }

    public Page<DeviceBasicData> findDeviceBasicDatas(String stationSystemCode, String deviceTypeCode, String evaluationIndexName, String evaluationIndexLevel, String weights, String deviceCategory, int page, int size) {
        Specification<DeviceBasicData> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<Predicate>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);

            if (!StringUtils.isEmpty(stationSystemCode)) {
                Predicate predicate = cb.equal(root.get("stationSystemCode").as(String.class), stationSystemCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(deviceTypeCode)) {
                Predicate predicate = cb.equal(root.get("deviceTypeCode").as(String.class), deviceTypeCode);
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(deviceCategory)) {
                Predicate predicate = cb.equal(root.get("deviceCategory").as(String.class), deviceCategory);
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
        Sort sort = Sort.by(Sort.Direction.ASC, "stationSystemCode");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<DeviceBasicData> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("DeviceBasicDataService.findDeviceBasicDatas。", e);
        }
        return pageData;
    }
}
