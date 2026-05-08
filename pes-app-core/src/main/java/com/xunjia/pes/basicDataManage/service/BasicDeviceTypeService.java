package com.xunjia.pes.basicDataManage.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.BasicDeviceType;
import com.xunjia.pes.basicDataManage.repository.IBasicDeviceTypeRepository;
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
public class BasicDeviceTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDeviceType.class);

    @Autowired
    IBasicDeviceTypeRepository repository;

    public ResponseData<Boolean> save(BasicDeviceType param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getDeviceTypeCode())) {
                BasicDeviceType BasicDeviceType = repository.findByDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(param.getDeviceTypeCode(), param.getDeviceCategory(), 0);
                if (BasicDeviceType != null) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
                    return resp;
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_NULL);
                return resp;
            }
            repository.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("BasicDeviceTypeService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(BasicDeviceType param, String originalCode) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getDeviceTypeCode())) {
                if (param.getDeviceTypeCode().equals(originalCode)) {
                    repository.save(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    BasicDeviceType sameObject = repository.findByDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(param.getDeviceTypeCode(), param.getDeviceCategory(), 0);
                    if (sameObject == null) {
                        repository.save(param);
                        resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                    } else {
                        resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
                    }
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_NULL);
            }
        } catch (Exception e) {
            LOGGER.error("BasicDeviceTypeService.update方法异常。", e);
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
            LOGGER.error("BasicDeviceTypeService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public BasicDeviceType findById(String id) {
        return repository.findById(id).get();
    }

    public Page<BasicDeviceType> findBasicDeviceTypes(String deviceTypeCode, String deviceTypeName, String deviceCategory, int page, int size) {
        Specification<BasicDeviceType> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<Predicate>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);

            if (!StringUtils.isEmpty(deviceTypeCode)) {
                Predicate predicate = cb.like(root.get("deviceTypeCode").as(String.class), "%" + deviceTypeCode + "%");
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(deviceTypeName)) {
                Predicate predicate = cb.like(root.get("deviceTypeName").as(String.class), "%" + deviceTypeName + "%");
                predicates.add(predicate);
            }

            if (!StringUtils.isEmpty(deviceCategory)) {
                Predicate predicate = cb.equal(root.get("deviceCategory").as(String.class), deviceCategory);
                predicates.add(predicate);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "deviceTypeCode");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<BasicDeviceType> pageData = null;
        try {
            pageData = repository.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("BasicDeviceTypeService.findBasicDeviceTypes。", e);
        }
        return pageData;
    }

    public List<BasicDeviceType> findAllBasicDeviceTypes(String deviceCategory){
        return repository.findByDeviceCategoryAndDeleteFlagOrderByDeviceTypeNameAsc(deviceCategory,0);
    }
}
