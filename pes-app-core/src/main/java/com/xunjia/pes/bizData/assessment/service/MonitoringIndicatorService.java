package com.xunjia.pes.bizData.assessment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.assessment.entity.Benchmark;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicator;
import com.xunjia.pes.bizData.assessment.mapper.MonitoringIndicatorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MonitoringIndicatorService {
    @Autowired
    private MonitoringIndicatorMapper mapper;

    public ResponseData<Boolean> save(MonitoringIndicator param){
        ResponseData<Boolean> resp;
        try {
            param.setDeleteFlag(0);
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        }catch (Exception ex){
            resp = ResponseData.getError(ex);
        }
        return resp;
    }
    public ResponseData<Boolean> update(MonitoringIndicator param){
        ResponseData<Boolean> resp;
        try {
            mapper.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        }catch (Exception ex){
            resp = ResponseData.getError(ex);
        }
        return resp;
    }
    public ResponseData<Boolean> deleteByIds(List<String> ids){
        ResponseData<Boolean> resp;
        try {
            LambdaQueryWrapper<MonitoringIndicator> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MonitoringIndicator::getId, ids);
            List<MonitoringIndicator> deleteEntities = mapper.selectList(wrapper);
            for (MonitoringIndicator param : deleteEntities) {
                param.setDeleteFlag(1);
                mapper.updateById(param);
            }
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public MonitoringIndicator findById(String id) {
        return mapper.selectById(id);
    }

    public List<MonitoringIndicator> findAll() {
        LambdaQueryWrapper<MonitoringIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MonitoringIndicator::getType);
        return mapper.selectList(wrapper);
    }

    public List<MonitoringIndicator> finByTypeAndItemOneKey(String type,String itemOneKey){
        LambdaQueryWrapper<MonitoringIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonitoringIndicator::getDeleteFlag,0);
        wrapper.eq(MonitoringIndicator::getType,type);
        if(StringUtils.isNotEmpty(itemOneKey)){
            wrapper.like(MonitoringIndicator::getMonitoringItemOne,itemOneKey);
        }
        return mapper.selectList(wrapper);
    }
}
