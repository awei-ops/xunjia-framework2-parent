package com.xunjia.pes.bizData.assessment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.assessment.entity.Requirements;
import com.xunjia.pes.bizData.assessment.mapper.RequirementsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class RequirementsService {
    @Autowired
    private RequirementsMapper mapper;

    public List<Requirements> findAllByType(String type){
        LambdaQueryWrapper<Requirements> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(type)){
            wrapper.eq(Requirements::getType,type);
        }
        wrapper.orderByAsc(Requirements::getOrderNo);
        return mapper.selectList(wrapper);
    }
}
