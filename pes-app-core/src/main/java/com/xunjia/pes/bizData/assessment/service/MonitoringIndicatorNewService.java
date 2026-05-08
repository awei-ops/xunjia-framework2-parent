package com.xunjia.pes.bizData.assessment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.mapper.MonitoringIndicatorNewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MonitoringIndicatorNewService {
    @Autowired
    private MonitoringIndicatorNewMapper mapper;

    public ResponseData<Boolean> save(MonitoringIndicatorNew param){
        ResponseData<Boolean> resp;
        try {
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        }catch (Exception ex){
            resp = ResponseData.getError(ex);
        }
        return resp;
    }
    public ResponseData<Boolean> update(MonitoringIndicatorNew param){
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
            LambdaQueryWrapper<MonitoringIndicatorNew> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MonitoringIndicatorNew::getId, ids);
            mapper.delete(wrapper);
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public MonitoringIndicatorNew findById(String id) {
        return mapper.selectById(id);
    }

    public List<MonitoringIndicatorNew> findAll() {
        QueryWrapper<MonitoringIndicatorNew> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("(CASE WHEN `order_no` IS NULL THEN 1 ELSE 0 END)");
        wrapper.orderByAsc("type_name").orderByAsc("order_no").orderByAsc("value_max").orderByAsc("monitoring_item");
        return mapper.selectList(wrapper);
    }

    public List<MonitoringIndicatorNew> findByParams(String type,String monitoringItem){
        QueryWrapper<MonitoringIndicatorNew> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(type)){
            wrapper.eq("type_name",type);
        }
        if(StringUtils.isNotEmpty(monitoringItem)){
            wrapper.eq("monitoring_item",monitoringItem);
        }
        wrapper.orderByAsc("(CASE WHEN `order_no` IS NULL THEN 1 ELSE 0 END)");
        wrapper.orderByAsc("type_name").orderByAsc("order_no").orderByAsc("value_max").orderByAsc("monitoring_item");
        return mapper.selectList(wrapper);
    }

    public PageVO<MonitoringIndicatorNew> getPageData(String type, int page, int rows) {
        PageVO<MonitoringIndicatorNew> pageVO = null;
        try {
            PageMethod.startPage(page, rows);
            QueryWrapper<MonitoringIndicatorNew> wrapper = new QueryWrapper<>();
            if(StringUtils.isNotEmpty(type)){
                wrapper.eq("type_name",type);
            }
            wrapper.orderByAsc("(CASE WHEN `order_no` IS NULL THEN 1 ELSE 0 END)");
            wrapper.orderByAsc("type_name").orderByAsc("order_no").orderByAsc("value_max").orderByAsc("monitoring_item");
            List<MonitoringIndicatorNew> dataList = mapper.selectList(wrapper);
            PageInfo<MonitoringIndicatorNew> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            pageVO = new PageVO<>();
        }
        return pageVO;
    }
}
